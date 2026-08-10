#!/usr/bin/env bash
# =========================================================
# Reverie backend smoke test
# =========================================================
# Exercises every endpoint, capability check and business
# rule against a running application.
#
# Usage:  bash smoke-test.sh
# Expects the API on http://localhost:8080 and the demo
# dataset loaded (dev profile).
# =========================================================

API="http://localhost:8080"
PASS=0
FAIL=0

check() {
  local label="$1" expected="$2" actual="$3"
  if [ "$expected" = "$actual" ]; then
    printf "  \033[32mPASS\033[0m  %-58s %s\n" "$label" "$actual"
    PASS=$((PASS + 1))
  else
    printf "  \033[31mFAIL\033[0m  %-58s expected %s, got %s\n" "$label" "$expected" "$actual"
    FAIL=$((FAIL + 1))
  fi
}

code() { curl -s -o /dev/null -w "%{http_code}" "$@"; }

login() {
  curl -s -X POST "$API/api/auth/login" -H "Content-Type: application/json" \
    -d "{\"email\":\"$1\",\"password\":\"$2\"}" \
    | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p'
}

json() { python -c "import sys,json;$1" 2>/dev/null; }

echo ""
echo "=============================================="
echo " REVERIE BACKEND SMOKE TEST"
echo "=============================================="

# ---------- Authentication ----------
echo ""
echo "AUTHENTICATION"
ADMIN=$(login "admin@reverie.com" "Admin123!")
ALEX=$(login  "alex@reverie.com"  "User123!")
EMMA=$(login  "emma@reverie.com"  "User123!")
DAN=$(login   "daniel@reverie.com" "User123!")

check "admin logs in"                  "200" "$(code -X POST $API/api/auth/login -H 'Content-Type: application/json' -d '{"email":"admin@reverie.com","password":"Admin123!"}')"
check "alex logs in"                   "200" "$(code -X POST $API/api/auth/login -H 'Content-Type: application/json' -d '{"email":"alex@reverie.com","password":"User123!"}')"
check "wrong password rejected"        "401" "$(code -X POST $API/api/auth/login -H 'Content-Type: application/json' -d '{"email":"alex@reverie.com","password":"nope"}')"
check "old demo password rejected"     "401" "$(code -X POST $API/api/auth/login -H 'Content-Type: application/json' -d '{"email":"admin@reverie.com","password":"admin123"}')"
check "unknown email rejected"         "401" "$(code -X POST $API/api/auth/login -H 'Content-Type: application/json' -d '{"email":"nobody@reverie.com","password":"User123!"}')"
check "registration rejects weak pw"   "400" "$(code -X POST $API/api/auth/register -H 'Content-Type: application/json' -d '{"username":"tester1","email":"tester1@reverie.com","password":"weak","displayName":"Tester"}')"

# ---------- Unauthenticated access ----------
echo ""
echo "UNAUTHENTICATED ACCESS  (must be 401, never 403)"
check "movies without token"           "401" "$(code $API/api/movies)"
check "profile without token"          "401" "$(code $API/api/users/me)"
check "garbage token"                  "401" "$(code -H 'Authorization: Bearer not.a.token' $API/api/movies)"
check "tampered signature"             "401" "$(code -H "Authorization: Bearer ${ALEX%?}X" $API/api/movies)"
check "swagger stays public"           "200" "$(code $API/swagger-ui/index.html)"
check "api-docs stays public"          "200" "$(code $API/v3/api-docs)"

# ---------- Capability enforcement ----------
echo ""
echo "CAPABILITY ENFORCEMENT"
check "USER reads movies"              "200" "$(code -H "Authorization: Bearer $ALEX" $API/api/movies)"
check "USER cannot create genre"       "403" "$(code -X POST $API/api/genres -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d '{"name":"Nope"}')"
check "USER cannot delete movie"       "403" "$(code -X DELETE $API/api/movies/00000000-0000-0000-0000-000000000001 -H "Authorization: Bearer $ALEX")"
check "USER cannot list users"         "403" "$(code -H "Authorization: Bearer $ALEX" $API/api/users)"
check "ADMIN can list users"           "200" "$(code -H "Authorization: Bearer $ADMIN" $API/api/users)"
check "ADMIN passes movie delete"      "404" "$(code -X DELETE $API/api/movies/00000000-0000-0000-0000-000000000001 -H "Authorization: Bearer $ADMIN")"

# ---------- Error handling ----------
echo ""
echo "ERROR HANDLING"
check "invalid uuid in path"           "400" "$(code -H "Authorization: Bearer $ALEX" $API/api/movies/not-a-uuid)"
check "malformed json body"            "400" "$(code -X POST $API/api/genres -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"name":')"
check "missing body"                   "400" "$(code -X POST $API/api/genres -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json')"
check "unknown endpoint"               "404" "$(code -H "Authorization: Bearer $ALEX" $API/api/nonexistent)"
check "unknown movie"                  "404" "$(code -H "Authorization: Bearer $ALEX" $API/api/movies/00000000-0000-0000-0000-000000000001)"

# ---------- Demo dataset ----------
echo ""
echo "DEMO DATASET"
check "12 genres"                      "12" "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/genres | grep -o '"uuid"' | wc -l | tr -d ' ')"
check "24 movies"                      "24" "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "alex has 9 watch logs"          "9"  "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/watch-logs?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "alex has 6 reviews"             "6"  "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/reviews/me?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "emma has 5 reviews"             "5"  "$(curl -s -H "Authorization: Bearer $EMMA" "$API/api/reviews/me?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "daniel has 4 reviews"           "4"  "$(curl -s -H "Authorization: Bearer $DAN" "$API/api/reviews/me?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"

# ---------- Pagination ----------
echo ""
echo "PAGINATION"
check "default page size"              "20" "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/movies | json "print(json.load(sys.stdin)['size'])")"
check "size=5 gives 5 pages"           "5"  "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=5" | json "print(json.load(sys.stdin)['totalPages'])")"
check "first page flagged first"       "True" "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?page=0&size=5" | json "print(json.load(sys.stdin)['first'])")"
check "last page flagged last"         "True" "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?page=4&size=5" | json "print(json.load(sys.stdin)['last'])")"
check "page beyond end is empty"       "0"  "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?page=99&size=5" | json "print(len(json.load(sys.stdin)['content']))")"
check "movies sorted by title"         "Alien" "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=1" | json "print(json.load(sys.stdin)['content'][0]['title'])")"

# ---------- Publication ----------
echo ""
echo "PUBLICATION"

ALIEN=$(curl -s -H "Authorization: Bearer $ADMIN" "$API/api/movies/all?size=100" | json "print([m['uuid'] for m in json.load(sys.stdin)['content'] if m['title']=='Alien'][0])")

check "USER cannot list all movies"    "403" "$(code -H "Authorization: Bearer $ALEX" $API/api/movies/all)"
check "USER cannot list deleted"       "403" "$(code -H "Authorization: Bearer $ALEX" $API/api/movies/deleted)"
check "ADMIN sees 24 to manage"        "24" "$(curl -s -H "Authorization: Bearer $ADMIN" "$API/api/movies/all?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "no deleted movies"              "0"  "$(curl -s -H "Authorization: Bearer $ADMIN" "$API/api/movies/deleted?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "USER cannot unpublish"          "403" "$(code -X POST $API/api/movies/$ALIEN/unpublish -H "Authorization: Bearer $ALEX")"
check "ADMIN unpublishes"              "200" "$(code -X POST $API/api/movies/$ALIEN/unpublish -H "Authorization: Bearer $ADMIN")"
check "catalogue drops to 23"          "23" "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "management still shows 24"      "24" "$(curl -s -H "Authorization: Bearer $ADMIN" "$API/api/movies/all?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"
check "ADMIN republishes"              "200" "$(code -X POST $API/api/movies/$ALIEN/publish -H "Authorization: Bearer $ADMIN")"
check "catalogue back to 24"           "24" "$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=100" | json "print(json.load(sys.stdin)['totalElements'])")"

# ---------- Privacy ----------
echo ""
echo "PRIVACY"
EMMA_UUID=$(curl -s -H "Authorization: Bearer $ADMIN" $API/api/users | json "print([u['uuid'] for u in json.load(sys.stdin) if u['username']=='emma'][0])")
check "own profile exposes email"      "True"  "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/users/me | json "print('email' in json.load(sys.stdin))")"
check "public profile hides email"     "False" "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/users/$EMMA_UUID | json "print('email' in json.load(sys.stdin))")"
check "user list hides emails"         "False" "$(curl -s -H "Authorization: Bearer $ADMIN" $API/api/users | json "print(any('email' in u for u in json.load(sys.stdin)))")"

# ---------- Business rules ----------
echo ""
echo "BUSINESS RULES"
GOODFELLAS=$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=100" | json "print([m['uuid'] for m in json.load(sys.stdin)['content'] if m['title']=='Goodfellas'][0])")
SHAWSHANK=$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/movies?size=100" | json "print([m['uuid'] for m in json.load(sys.stdin)['content'] if m['title']=='The Shawshank Redemption'][0])")
check "review needs a watch log"       "400" "$(code -X POST $API/api/reviews -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d "{\"movieUuid\":\"$GOODFELLAS\",\"rating\":5}")"
check "no duplicate review"            "409" "$(code -X POST $API/api/reviews -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d "{\"movieUuid\":\"$SHAWSHANK\",\"rating\":5}")"
check "review needs rating or text"    "400" "$(code -X POST $API/api/reviews -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d "{\"movieUuid\":\"$GOODFELLAS\"}")"
check "rating above 10 rejected"       "400" "$(code -X POST $API/api/reviews -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d "{\"movieUuid\":\"$SHAWSHANK\",\"rating\":11}")"

# ---------- Ownership ----------
echo ""
echo "OWNERSHIP"
ALEX_REVIEW=$(curl -s -H "Authorization: Bearer $ALEX" "$API/api/reviews/me?size=1" | json "print(json.load(sys.stdin)['content'][0]['uuid'])")
check "owner may edit own review"      "200" "$(code -X PUT $API/api/reviews/$ALEX_REVIEW -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d '{"rating":9,"reviewText":"smoke test"}')"
check "others may not edit it"         "403" "$(code -X PUT $API/api/reviews/$ALEX_REVIEW -H "Authorization: Bearer $EMMA" -H 'Content-Type: application/json' -d '{"rating":1}')"
check "others may not delete it"       "403" "$(code -X DELETE $API/api/reviews/$ALEX_REVIEW -H "Authorization: Bearer $EMMA")"

# ---------- Role management ----------
echo ""
echo "ROLE MANAGEMENT"
ADMIN_UUID=$(curl -s -H "Authorization: Bearer $ADMIN" $API/api/users/me | json "print(json.load(sys.stdin)['uuid'])")
DAN_UUID=$(curl -s -H "Authorization: Bearer $DAN" $API/api/users/me | json "print(json.load(sys.stdin)['uuid'])")
check "admin cannot demote self"       "409" "$(code -X PUT $API/api/users/$ADMIN_UUID/role -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"roleName":"USER"}')"
check "unknown role rejected"          "404" "$(code -X PUT $API/api/users/$DAN_UUID/role -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"roleName":"WIZARD"}')"
check "user cannot promote anyone"     "403" "$(code -X PUT $API/api/users/$DAN_UUID/role -H "Authorization: Bearer $ALEX" -H 'Content-Type: application/json' -d '{"roleName":"ADMIN"}')"
check "admin promotes daniel"          "200" "$(code -X PUT $API/api/users/$DAN_UUID/role -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"roleName":"ADMIN"}')"
DAN2=$(login "daniel@reverie.com" "User123!")
check "daniel now has admin rights"    "200" "$(code -H "Authorization: Bearer $DAN2" $API/api/users)"
check "admin demotes daniel back"      "200" "$(code -X PUT $API/api/users/$DAN_UUID/role -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"roleName":"USER"}')"

# ---------- Soft delete and restore ----------
echo ""
echo "SOFT DELETE AND RESTORE"
G=$(curl -s -X POST $API/api/genres -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"name":"SmokeTestGenre"}')
GU=$(echo "$G" | sed -n 's/.*"uuid":"\([^"]*\)".*/\1/p')
check "genre created"                  "13"  "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/genres | grep -o '"uuid"' | wc -l | tr -d ' ')"
check "duplicate name rejected"        "409" "$(code -X POST $API/api/genres -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"name":"SmokeTestGenre"}')"
check "case-insensitive duplicate"     "409" "$(code -X POST $API/api/genres -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"name":"smoketestgenre"}')"
check "restore before delete is 409"   "409" "$(code -X POST $API/api/genres/$GU/restore -H "Authorization: Bearer $ADMIN")"
check "soft delete"                    "204" "$(code -X DELETE $API/api/genres/$GU -H "Authorization: Bearer $ADMIN")"
check "deleted genre is gone"          "12"  "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/genres | grep -o '"uuid"' | wc -l | tr -d ' ')"
check "name is free again"             "201" "$(code -X POST $API/api/genres -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"name":"SmokeTestGenre"}')"
check "restore now conflicts"          "409" "$(code -X POST $API/api/genres/$GU/restore -H "Authorization: Bearer $ADMIN")"
NU=$(curl -s -H "Authorization: Bearer $ADMIN" $API/api/genres | json "print([g['uuid'] for g in json.load(sys.stdin) if g['name']=='SmokeTestGenre'][0])")
code -X DELETE $API/api/genres/$NU -H "Authorization: Bearer $ADMIN" > /dev/null
check "restore original succeeds"      "200" "$(code -X POST $API/api/genres/$GU/restore -H "Authorization: Bearer $ADMIN")"
code -X DELETE $API/api/genres/$GU -H "Authorization: Bearer $ADMIN" > /dev/null
check "cleanup left 12 genres"         "12"  "$(curl -s -H "Authorization: Bearer $ALEX" $API/api/genres | grep -o '"uuid"' | wc -l | tr -d ' ')"

# ---------- CORS ----------
echo ""
echo "CORS"
check "preflight from vite origin"     "200" "$(code -X OPTIONS $API/api/movies -H 'Origin: http://localhost:5173' -H 'Access-Control-Request-Method: GET')"
check "preflight on login"             "200" "$(code -X OPTIONS $API/api/auth/login -H 'Origin: http://localhost:5173' -H 'Access-Control-Request-Method: POST')"
check "unknown origin rejected"        "403" "$(code -X OPTIONS $API/api/movies -H 'Origin: http://evil.example.com' -H 'Access-Control-Request-Method: GET')"

# ---------- Summary ----------
echo ""
echo "=============================================="
printf " PASSED: %d   FAILED: %d\n" "$PASS" "$FAIL"
echo "=============================================="
echo ""
[ "$FAIL" -eq 0 ] || exit 1

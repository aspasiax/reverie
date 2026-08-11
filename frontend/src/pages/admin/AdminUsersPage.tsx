import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { UserCheck, UserX } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { UserAdmin } from '@/types/api'
import { Button } from '@/components/ui/button'

/**
 * The roles an account can hold.
 *
 * These mirror the roles seeded in the database. The list is closed: roles
 * are created by migration rather than through the interface, because each
 * one only means something once capabilities have been granted to it.
 */
const ROLES = ['USER', 'ADMIN']

/** Retrieves every account, ordered by display name on the server. */
async function fetchUsers() {
    const { data } = await api.get<UserAdmin[]>('/api/users')
    return data
}

/**
 * Lets an administrator see the accounts and change what they may do.
 *
 * Changing a role changes a set of capabilities, not a label: the new
 * permissions apply to the next request that account makes. Disabling goes
 * further and ends the session immediately, because the token is checked
 * against the account on every request rather than trusted on its own.
 *
 * Neither action is offered on the administrator's own row. The server
 * refuses both, and this screen reflects that rather than restating it.
 */
export function AdminUsersPage() {
    const queryClient = useQueryClient()
    const { user, can } = useAuth()

    const { data: users, isPending, isError, error } = useQuery({
        queryKey: ['users'],
        queryFn: fetchUsers,
    })

    /** Refreshes the listing after any change to an account. */
    function invalidateUsers() {
        queryClient.invalidateQueries({ queryKey: ['users'] })
    }

    const changeRole = useMutation({
        mutationFn: ({ uuid, roleName }: { uuid: string; roleName: string }) =>
            api.put(`/api/users/${uuid}/role`, { roleName }),
        onSuccess: invalidateUsers,
    })

    /*
     * Enabling and withdrawing an account are the same decision seen from
     * two sides, so they share one mutation and differ only in the address.
     */
    const setEnabled = useMutation({
        mutationFn: ({ uuid, enabled }: { uuid: string; enabled: boolean }) =>
            api.post(`/api/users/${uuid}/${enabled ? 'enable' : 'disable'}`),
        onSuccess: invalidateUsers,
    })

    const failure = changeRole.error ?? setEnabled.error

    return (
        <div className="space-y-4">
            {isPending && <p className="text-muted-foreground">Loading accounts…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {failure !== null && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(failure)}
                </p>
            )}

            {users !== undefined && (
                <>
                    <p className="text-sm text-muted-foreground">
                        {users.length} accounts
                    </p>

                    <ul className="divide-y rounded-lg border">
                        {users.map((account) => {
                            const isSelf = account.uuid === user?.uuid

                            return (
                                <li
                                    key={account.uuid}
                                    className="flex flex-wrap items-center gap-3 p-3"
                                >
                                    <div className="min-w-0 flex-1">
                                        <p className="truncate font-medium">
                                            {account.displayName}
                                            {isSelf && (
                                                <span className="ml-2 text-xs font-normal text-muted-foreground">
                                                    you
                                                </span>
                                            )}
                                        </p>
                                        <p className="truncate text-sm text-muted-foreground">
                                            @{account.username}
                                        </p>
                                    </div>

                                    {!account.enabled && (
                                        <span className="shrink-0 rounded-full border border-dashed px-2 py-0.5 text-xs text-muted-foreground">
                                            Disabled
                                        </span>
                                    )}

                                    <div className="flex shrink-0 gap-1">
                                        {ROLES.map((role) => (
                                            <Button
                                                key={role}
                                                size="sm"
                                                variant={
                                                    account.role === role ? 'default' : 'outline'
                                                }
                                                onClick={() =>
                                                    changeRole.mutate({
                                                        uuid: account.uuid,
                                                        roleName: role,
                                                    })
                                                }
                                                disabled={
                                                    isSelf ||
                                                    !can('USER_UPDATE') ||
                                                    account.role === role ||
                                                    changeRole.isPending
                                                }
                                            >
                                                {role}
                                            </Button>
                                        ))}
                                    </div>

                                    {can('USER_DISABLE') && (
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onClick={() =>
                                                setEnabled.mutate({
                                                    uuid: account.uuid,
                                                    enabled: !account.enabled,
                                                })
                                            }
                                            disabled={isSelf || setEnabled.isPending}
                                            aria-label={
                                                account.enabled
                                                    ? `Disable ${account.displayName}`
                                                    : `Enable ${account.displayName}`
                                            }
                                        >
                                            {account.enabled ? (
                                                <UserCheck className="size-4" />
                                            ) : (
                                                <UserX className="size-4 text-destructive" />
                                            )}
                                        </Button>
                                    )}
                                </li>
                            )
                        })}
                    </ul>
                </>
            )}
        </div>
    )
}
import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation } from '@tanstack/react-query'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { UpdateUserRequest, UserProfile } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'

/**
 * Sends the changed profile fields to the server.
 *
 * @param request the fields the user may edit
 */
async function updateProfile(request: UpdateUserRequest) {
    const { data } = await api.put<UserProfile>('/api/users/me', request)
    return data
}

/**
 * The profile of the authenticated user.
 *
 * The username, the email address and the role are shown but cannot be
 * changed here: the first two identify the account and the third is granted
 * by an administrator.
 */
export function ProfilePage() {
    const { user, refreshUser } = useAuth()

    const [displayName, setDisplayName] = useState(user?.displayName ?? '')
    const [bio, setBio] = useState(user?.bio ?? '')
    const [imageUrl, setImageUrl] = useState(user?.profileImageUrl ?? '')

    const save = useMutation({
        mutationFn: () =>
            updateProfile({
                displayName: displayName.trim(),
                bio: bio.trim() === '' ? null : bio.trim(),
                profileImageUrl: imageUrl.trim() === '' ? null : imageUrl.trim(),
            }),
        onSuccess: refreshUser,
    })

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        save.mutate()
    }

    if (user === null) {
        return null
    }

    return (
        <div className="mx-auto max-w-2xl space-y-8 px-4 py-6 sm:px-6">
            <h1 className="text-2xl font-semibold">Profile</h1>

            {/* Fields the account owns rather than the user. */}
            <dl className="grid grid-cols-[auto_1fr] gap-x-6 gap-y-2 text-sm">
                <dt className="text-muted-foreground">Username</dt>
                <dd>{user.username}</dd>

                <dt className="text-muted-foreground">Email</dt>
                <dd>{user.email}</dd>

                <dt className="text-muted-foreground">Role</dt>
                <dd>
          <span className="rounded-full border px-2 py-0.5 text-xs">
            {user.role}
          </span>
                </dd>

                <dt className="text-muted-foreground">Member since</dt>
                <dd>{user.createdAt.slice(0, 10)}</dd>
            </dl>

            <form onSubmit={handleSubmit} className="space-y-4 rounded-lg border p-6">
                <div className="space-y-2">
                    <Label htmlFor="display-name">Display name</Label>
                    <Input
                        id="display-name"
                        value={displayName}
                        onChange={(event) => setDisplayName(event.target.value)}
                        required
                        minLength={2}
                        maxLength={150}
                    />
                </div>

                <div className="space-y-2">
                    <Label htmlFor="bio">Biography</Label>
                    <Textarea
                        id="bio"
                        value={bio}
                        onChange={(event) => setBio(event.target.value)}
                        placeholder="A sentence or two about the films you like."
                        rows={3}
                        maxLength={500}
                    />
                </div>

                <div className="space-y-2">
                    <Label htmlFor="image-url">Profile image address</Label>
                    <Input
                        id="image-url"
                        type="url"
                        value={imageUrl}
                        onChange={(event) => setImageUrl(event.target.value)}
                        placeholder="https://…"
                        maxLength={1024}
                    />
                </div>

                {save.isError && (
                    <p role="alert" className="text-sm text-destructive">
                        {errorMessage(save.error)}
                    </p>
                )}

                {save.isSuccess && (
                    <p className="text-sm text-green-500">Profile updated.</p>
                )}

                <Button type="submit" disabled={save.isPending}>
                    {save.isPending ? 'Saving…' : 'Save changes'}
                </Button>
            </form>
        </div>
    )
}
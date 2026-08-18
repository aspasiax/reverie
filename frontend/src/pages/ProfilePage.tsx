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
import { PasswordForm } from '@/components/PasswordForm'
import { UserStatistics } from '@/components/UserStatistics'

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
 * The page is arranged as three answers: who you are, how you appear, and
 * how you sign in. The username, the email address and the role sit in the
 * first because they identify the account rather than describe the person:
 * the first two cannot be changed, and the third is granted by an
 * administrator.
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
        <div className="mx-auto max-w-2xl space-y-10 px-4 py-8 sm:px-6">
            <header className="flex items-center gap-4">
                <div className="flex size-16 shrink-0 items-center justify-center overflow-hidden rounded-full border bg-muted text-xl font-semibold">
                    {user.profileImageUrl !== null ? (
                        <img
                            src={user.profileImageUrl}
                            alt=""
                            className="size-full object-cover"
                        />
                    ) : (
                        user.displayName.charAt(0).toUpperCase()
                    )}
                </div>

                <div className="min-w-0 space-y-1">
                    <h1 className="truncate text-2xl font-semibold tracking-tight">
                        {user.displayName}
                    </h1>

                    <p className="truncate text-sm text-muted-foreground">
                        @{user.username} · {user.email}
                    </p>

                    <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
                        <span className="rounded-full border px-2 py-0.5">{user.role}</span>
                        <span>member since {user.createdAt.slice(0, 10)}</span>
                    </div>
                </div>
            </header>

            <UserStatistics uuid={user.uuid} />

            <section className="space-y-4">
                <div className="space-y-1">
                    <h2 className="text-lg font-medium">Profile</h2>
                    <p className="text-sm text-muted-foreground">
                        How you appear to other readers.
                    </p>
                </div>

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
            </section>

            <section className="space-y-4">
                <div className="space-y-1">
                    <h2 className="text-lg font-medium">Security</h2>
                    <p className="text-sm text-muted-foreground">
                        Change the password you sign in with.
                    </p>
                </div>

                <PasswordForm />
            </section>
        </div>
    )
}
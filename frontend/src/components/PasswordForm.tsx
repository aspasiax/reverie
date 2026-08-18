import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation } from '@tanstack/react-query'
import { api, errorMessage } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

/**
 * Lets the signed in user replace the password they sign in with.
 *
 * The current password is asked for even though the session is already
 * open: a token proves who started the session, not who is at the keyboard
 * now. Getting it wrong is answered with a plain rejection rather than an
 * authentication failure, so a typo never ends the session.
 *
 * Tokens issued before the change keep working until they expire. Reverie
 * authenticates without server side sessions, so there is no register of
 * live tokens to revoke.
 */
export function PasswordForm() {
    const [currentPassword, setCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')

    const change = useMutation({
        mutationFn: () =>
            api.put('/api/users/me/password', { currentPassword, newPassword }),
        /*
         * The fields are emptied on success: a password left sitting in an
         * input is one browser autofill away from somewhere it should not be.
         */
        onSuccess: () => {
            setCurrentPassword('')
            setNewPassword('')
        },
    })

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        change.mutate()
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-4 rounded-lg border p-6">
            <div className="space-y-2">
                <Label htmlFor="current-password">Current password</Label>
                <Input
                    id="current-password"
                    type="password"
                    value={currentPassword}
                    onChange={(event) => setCurrentPassword(event.target.value)}
                    required
                    autoComplete="current-password"
                />
            </div>

            <div className="space-y-2">
                <Label htmlFor="new-password">New password</Label>
                <Input
                    id="new-password"
                    type="password"
                    value={newPassword}
                    onChange={(event) => setNewPassword(event.target.value)}
                    required
                    minLength={8}
                    maxLength={100}
                    autoComplete="new-password"
                />
                <p className="text-xs text-muted-foreground">
                    At least eight characters, with an upper and a lower case letter, a
                    digit and a symbol.
                </p>
            </div>

            {change.isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(change.error)}
                </p>
            )}

            {change.isSuccess && (
                <p className="text-sm text-green-500">Password changed.</p>
            )}

            <Button type="submit" disabled={change.isPending}>
                {change.isPending ? 'Changing…' : 'Change password'}
            </Button>
        </form>
    )
}
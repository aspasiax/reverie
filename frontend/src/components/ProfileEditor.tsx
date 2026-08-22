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
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'
import { Avatar } from '@/components/Avatar'
import { FavouriteFilmPicker } from '@/components/FavouriteFilmPicker'

interface ProfileEditorProps {
    /** Whether the dialog is currently shown. */
    open: boolean
    /** Reports a request to open or close the dialog. */
    onOpenChange: (open: boolean) => void
}

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
 * Changes how the signed in reader appears to everyone else.
 *
 * The fields live behind a dialog so that the profile can be a profile.
 * A page that shows you a form is about settings; a page that shows you
 * what other people see is about you, and this is the way back to editing
 * it without the form standing in front of it all the time.
 *
 * The caller remounts this component whenever it opens, so the inputs
 * always start from what is currently saved rather than from whatever was
 * typed and abandoned last time.
 */
export function ProfileEditor({ open, onOpenChange }: ProfileEditorProps) {
    const { user, refreshUser } = useAuth()

    const [displayName, setDisplayName] = useState(user?.displayName ?? '')
    const [bio, setBio] = useState(user?.bio ?? '')
    const [imageUrl, setImageUrl] = useState(user?.profileImageUrl ?? '')
    const [favourite, setFavourite] = useState(user?.favouriteMovie?.uuid ?? null)

    const save = useMutation({
        mutationFn: () =>
            updateProfile({
                displayName: displayName.trim(),
                bio: bio.trim() === '' ? null : bio.trim(),
                profileImageUrl: imageUrl.trim() === '' ? null : imageUrl.trim(),
                /*
                 * Always sent, never omitted. The request replaces the profile
                 * rather than patching it, so leaving the field out is how a
                 * favourite is cleared -- and would clear it on every save.
                 */
                favouriteMovieUuid: favourite,
            }),
        onSuccess: async () => {
            await refreshUser()
            onOpenChange(false)
        },
    })

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        save.mutate()
    }

    if (user === null) {
        return null
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-h-[90vh] overflow-y-auto">
                <form onSubmit={handleSubmit} className="space-y-4">
                    <DialogHeader>
                        <DialogTitle>Edit profile</DialogTitle>
                        <DialogDescription>
                            Everything here is visible to other readers.
                        </DialogDescription>
                    </DialogHeader>

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

                    <div className="flex items-start gap-3">
                        {/*
                          * The preview follows what is typed rather than what is
                          * saved, so a wrong address is visible before it is kept.
                          */}
                        <Avatar
                            name={displayName === '' ? user.displayName : displayName}
                            seed={user.username}
                            imageUrl={imageUrl.trim() === '' ? null : imageUrl.trim()}
                            className="size-12 text-base"
                        />

                        <div className="flex-1 space-y-2">
                            <Label htmlFor="image-url">Profile image address</Label>
                            <Input
                                id="image-url"
                                type="url"
                                value={imageUrl}
                                onChange={(event) => setImageUrl(event.target.value)}
                                placeholder="https://…"
                                maxLength={1024}
                            />
                            <p className="text-xs text-muted-foreground">
                                Leave this empty to use your initial.
                            </p>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label>Favourite film</Label>

                        <FavouriteFilmPicker value={favourite} onChange={setFavourite} />

                        <p className="text-xs text-muted-foreground">
                            Chosen from the films you have watched. Click it again to
                            choose none.
                        </p>
                    </div>

                    {save.isError && (
                        <p role="alert" className="text-sm text-destructive">
                            {errorMessage(save.error)}
                        </p>
                    )}

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="ghost"
                            onClick={() => onOpenChange(false)}
                        >
                            Cancel
                        </Button>
                        <Button type="submit" disabled={save.isPending}>
                            {save.isPending ? 'Saving…' : 'Save changes'}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
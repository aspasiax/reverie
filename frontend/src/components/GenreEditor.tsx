import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { api, errorMessage } from '@/lib/api'
import { genreIconNames } from '@/lib/genreIcons'
import { cn } from '@/lib/utils'
import type { Genre } from '@/types/api'
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
import { GenreIcon } from '@/components/GenreIcon'

interface GenreEditorProps {
    /** Whether the dialog is currently shown. */
    open: boolean
    /** Reports a request to open or close the dialog. */
    onOpenChange: (open: boolean) => void
    /** The genre being changed, or null when creating a new one. */
    existing: Genre | null
}

/** Turns a blank field into null, which is what the API expects for absent values. */
function optional(value: string) {
    const trimmed = value.trim()
    return trimmed === '' ? null : trimmed
}

/**
 * Creates a new genre or changes an existing one.
 *
 * The same form serves both operations because the two requests carry
 * identical fields; only the method and the address differ. The caller
 * remounts this component whenever the edited genre changes, so the inputs
 * always start from the values of the genre actually being edited.
 */
export function GenreEditor({ open, onOpenChange, existing }: GenreEditorProps) {
    const queryClient = useQueryClient()

    const [name, setName] = useState(existing?.name ?? '')
    const [description, setDescription] = useState(existing?.description ?? '')
    const [icon, setIcon] = useState(existing?.icon ?? '')
    const [color, setColor] = useState(existing?.color ?? '#6C63FF')

    const save = useMutation({
        mutationFn: () => {
            const body = {
                name: name.trim(),
                description: optional(description),
                icon: optional(icon),
                color: optional(color),
            }

            return existing === null
                ? api.post('/api/genres', body)
                : api.put(`/api/genres/${existing.uuid}`, body)
        },
        onSuccess: () => {
            /*
             * Films carry their genres inside their own response, so a renamed
             * genre would keep showing its old badge until those queries were
             * refetched on their own.
             */
            queryClient.invalidateQueries({ queryKey: ['genres'] })
            queryClient.invalidateQueries({ queryKey: ['movies'] })
            queryClient.invalidateQueries({ queryKey: ['movie'] })
            onOpenChange(false)
        },
    })

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        save.mutate()
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <DialogHeader>
                        <DialogTitle>
                            {existing === null ? 'New genre' : 'Edit genre'}
                        </DialogTitle>
                        <DialogDescription>
                            Genres group the catalogue and appear as badges on every film.
                        </DialogDescription>
                    </DialogHeader>

                    <div className="space-y-2">
                        <Label htmlFor="genre-name">Name</Label>
                        <Input
                            id="genre-name"
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                            placeholder="Science Fiction"
                            required
                            maxLength={100}
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="genre-description">Description</Label>
                        <Textarea
                            id="genre-description"
                            value={description}
                            onChange={(event) => setDescription(event.target.value)}
                            placeholder="What kind of stories belong here?"
                            rows={3}
                            maxLength={500}
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="genre-color">Colour</Label>
                        <Input
                            id="genre-color"
                            type="color"
                            value={color}
                            onChange={(event) => setColor(event.target.value)}
                            className="h-9 w-16 p-1"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label>Icon</Label>

                        {/*
                          * A closed set rather than a text field: only these can
                          * be drawn, so only these can be chosen. Each is shown
                          * in the colour picked above, which is how the genre
                          * will actually appear.
                          */}
                        <div className="flex flex-wrap gap-1">
                            {genreIconNames.map((name) => {
                                const isSelected = icon === name

                                return (
                                    <button
                                        key={name}
                                        type="button"
                                        onClick={() => setIcon(isSelected ? '' : name)}
                                        aria-label={name}
                                        aria-pressed={isSelected}
                                        className={cn(
                                            'flex size-9 items-center justify-center rounded-md border transition-colors',
                                            isSelected
                                                ? 'border-primary bg-accent'
                                                : 'hover:bg-accent/50',
                                        )}
                                    >
                                        <GenreIcon name={name} color={color} className="size-4" />
                                    </button>
                                )
                            })}
                        </div>
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
                            {save.isPending ? 'Saving…' : 'Save'}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
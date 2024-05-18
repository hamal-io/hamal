import React, {FC, useEffect, useState} from "react";
import {useRecipeGet} from "@/hook/recipe.ts";
import {useNavigate} from "react-router-dom";
import {DialogContent, DialogDescription, DialogHeader} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useAdhoc, useNamespaceList} from "@/hook";
import {RecipeListItem} from "@/types/recipe.ts";
import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import {Loader2} from "lucide-react";


type Props = {
    item: RecipeListItem
    onClose: () => void
}
export const RecipeDialog: FC<Props> = ({item, onClose}) => {
    const [uiState] = useUiState()
    const navigate = useNavigate()
    const [namespace, setNamespace] = useState(uiState.namespaceId)
    const [listNamespaces, namespaceList, loading] = useNamespaceList()
    const [adhoc] = useAdhoc()
    const [getRecipe, recipe] = useRecipeGet()
    const [isLoading, setLoading] = useState(false)

    useEffect(() => {
        if (uiState.workspaceId != null) {
            listNamespaces(uiState.workspaceId)
        }
    }, []);

    useEffect(() => {
        if (recipe) {
            try {
                adhoc(namespace, recipe.value, "Lua54")
            } catch (e) {
                console.log(e)
            } finally {
                onClose()
            }
        }
    }, [recipe]);

    async function deployAction() {
        setLoading(true)
        try {
            getRecipe(item.id)
        } catch (e) {
            console.log(e)
        } finally {
            setLoading(false)
            onClose()
        }
    }


    if (namespaceList === null || loading) return "Loading..."

    return (
        <DialogContent>
            <DialogHeader>{item.name}</DialogHeader>
            <DialogDescription>{item.description}</DialogDescription>
            <div/>
            <div className="flex flex-row justify-between items-center gap-4">
                <Select
                    value={namespace}
                    onValueChange={(ns) => setNamespace(ns)}>
                    <SelectTrigger>
                        <SelectValue/>
                    </SelectTrigger>
                    <SelectContent>
                        <SelectGroup>
                            <SelectLabel>Deploy to Namespace: </SelectLabel>
                            {namespaceList.namespaces.map(namespace =>
                                <SelectItem
                                    key={namespace.id}
                                    value={namespace.id}>{namespace.name}
                                </SelectItem>
                            )}
                        </SelectGroup>
                    </SelectContent>
                </Select>
                <Button type={"submit"} size="sm" onClick={deployAction}>
                    {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                    Deploy
                </Button>
                <Button size={"sm"} onClick={() => {
                    navigate(`/recipes/editor/${item.id}`)
                }} variant="secondary">
                    Edit
                </Button>
            </div>
        </DialogContent>

    )
}
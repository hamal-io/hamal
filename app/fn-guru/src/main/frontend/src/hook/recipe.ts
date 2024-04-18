import {Recipe, RecipeCreateRequested, RecipeList, RecipeUpdateRequested} from "@/types/recipe.ts";
import {useAuth} from "@/hook/auth.ts";
import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {useCallback} from "react";


type RecipeCreateAction = (name: string, value: string, description: string, abortController?: AbortController) => void
export const useRecipeCreate = (): [RecipeCreateAction, RecipeCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submitted, loading, error] = usePost<RecipeCreateRequested>()
    const fn = useCallback(async (name: string, value: string, description: string, abortController?: AbortController) =>
        post(`/v1/recipes`, {
            name: name,
            inputs: {},
            value: value,
            description: description
        }, abortController), [auth]
    )
    return [fn, submitted, loading, error]
}

type RecipeGetAction = (bpId: string, abortController?: AbortController) => void
export const useRecipeGet = (): [RecipeGetAction, Recipe, boolean, Error] => {
    const [auth] = useAuth()
    const [get, recipe, loading, error] = useGet<Recipe>()
    const fn = useCallback(async (bpId: string, abortController?: AbortController) =>
        get(`/v1/recipes/${bpId}`, abortController), [auth])
    return [fn, recipe, loading, error]
}


type RecipeUpdateAction = (id: string, name: string, value: string, description: string, abortController?: AbortController) => void
export const useRecipeUpdate = (): [RecipeUpdateAction, RecipeUpdateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submitted, loading, error] = usePatch<RecipeUpdateRequested>()
    const fn = useCallback(async (id: string, name: string, value: string, description: string, abortController?: AbortController) =>
        patch(`/v1/recipes/${id}`, {
            name: name,
            inputs: {},
            value: value,
            description: description
        }, abortController), [auth])
    return [fn, submitted, loading, error]
}


type RecipeListAction = (abortController?: AbortController) => void
export const useRecipeList = (): [RecipeListAction, RecipeList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, list, loading, error] = useGet<RecipeList>()
    const fn = useCallback(async (abortController?: AbortController) =>
        get(`/v1/recipes`, abortController), [auth])
    return [fn, list, loading, error]
}
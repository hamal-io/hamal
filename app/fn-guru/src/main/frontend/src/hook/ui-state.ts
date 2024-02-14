import useLocalStorageState from "use-local-storage-state";
import {UI_STATE_KEY, UiState} from "@/types/ui-state.ts";
import {useCallback} from "react";
import {string} from "zod";


const unauthorized: UiState = {
    type: 'Unauthorized',
    workspaceId: '',
    namespaceId: '',
}

export const useUiState = () => {
    return useLocalStorageState<UiState>(UI_STATE_KEY, {
        defaultValue: {...unauthorized}
    })
}

type InitUiStateAction = (workspaceId: string, namespaceId: string) => void
export const useInitUiState = (): [InitUiStateAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback((workspaceId: string, namespaceId: string) => {
        setUiState({
            type: 'Authorized',
            workspaceId,
            namespaceId,
        })
    }, [uiState])

    return [fn]
}

type ResetUiStateAction = () => void
export const useResetUiState = (): [ResetUiStateAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback(() => {
        setUiState({...unauthorized})
    }, [uiState])

    return [fn]
}

type ChangeGroupAction = (workspaceId: string, groupName: string) => void
export const useChangeGroup = (): [ChangeGroupAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback((workspaceId: string) => {
        setUiState({
            ...uiState,
            workspaceId
        })
    }, [uiState])

    return [fn]
}

type ChangeNamespaceAction = (namespaceId: string, namespaceName: string) => void
export const useChangeNamespace = (): [ChangeNamespaceAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback((namespaceId: string) => {
        setUiState({
            ...uiState,
            namespaceId
        })
    }, [uiState])

    return [fn]
}
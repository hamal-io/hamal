import useLocalStorageState from "use-local-storage-state";
import {UI_STATE_KEY, UiState} from "@/types/ui-state.ts";
import {useCallback, useEffect} from "react";
import {useNamespaceGet} from "@/hook/namespace.ts";


const unauthorized: UiState = {
    type: 'Unauthorized',
    workspaceId: '',
    namespaceId: '',
    features: ''
}

export const useUiState = () => {
    return useLocalStorageState<UiState>(UI_STATE_KEY, {
        defaultValue: {...unauthorized}
    })
}

type InitUiStateAction = (workspaceId: string, namespaceId: string, features: string) => void
export const useInitUiState = (): [InitUiStateAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback((workspaceId: string, namespaceId: string, features: string) => {
        setUiState({
            type: 'Authorized',
            workspaceId,
            namespaceId
        })
    }, [uiState])

    return [fn]
}

type ResetUiStateAction = () => void
export const useResetUiState = (): [ResetUiStateAction] => {
    const [_, setUiState] = useUiState()

    const fn = useCallback(() => {
        setUiState({...unauthorized})
    }, [setUiState])

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

type ChangeNamespaceAction = (_namespaceId: string, namespaceName: string) => void
export const useChangeNamespace = (): [ChangeNamespaceAction] => {
    const [uiState, setUiState] = useUiState()
    const [get, namespace] = useNamespaceGet()

    const fn = useCallback((_namespaceId: string) => {
        get(_namespaceId)
    }, [uiState])

    useEffect(() => {
        if (namespace) {
            const namespaceId = namespace.id
            const features = namespace.features
            setUiState({
                ...uiState,
                namespaceId,
                features
            })
        }
    }, [namespace]);

    return [fn]
}
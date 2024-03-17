import useLocalStorageState from "use-local-storage-state";
import {UI_STATE_KEY, UiState} from "@/types/ui-state.ts";
import {useCallback} from "react";


const unauthorized: UiState = {
    type: 'Unauthorized',
    theme: 'light'
}

export const useUiState = () => {
    return useLocalStorageState<UiState>(UI_STATE_KEY, {
        defaultValue: {...unauthorized}
    })
}

type InitUiStateAction = (workspaceId: string, namespaceId: string) => void
export const useInitUiState = (): [InitUiStateAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback((theme: string) => {
        setUiState({
            type: 'Authorized',
            theme,
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

type ChangeThemeAction = (theme: string) => void
export const useChangeTheme = (): [ChangeThemeAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback((theme: string) => {
        setUiState({
            ...uiState,
            theme
        })
    }, [uiState])

    return [fn]
}
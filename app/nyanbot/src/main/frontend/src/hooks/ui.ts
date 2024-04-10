import useLocalStorageState from "use-local-storage-state";
import {UI_STATE_KEY, UiState} from "@/types/ui.ts";
import {useCallback} from "react";


const defaultState: UiState = {
    theme: 'light',
}

export const useUiState = () => {
    return useLocalStorageState<UiState>(UI_STATE_KEY, {
        defaultValue: {...defaultState}
    })
}

type InitUiStateAction = () => void
export const useInitUiState = (): [InitUiStateAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback(() => {
        setUiState({theme: uiState.theme || 'light'})
    }, [uiState])

    return [fn]
}

type ResetUiStateAction = () => void
export const useResetUiState = (): [ResetUiStateAction] => {
    const [_, setUiState] = useUiState()

    const fn = useCallback(() => {
        setUiState({...defaultState})
    }, [setUiState])

    return [fn]
}

type ChangeThemeAction = () => void
export const useChangeTheme = (): [ChangeThemeAction] => {
    const [uiState, setUiState] = useUiState()

    const fn = useCallback(() => {
        setUiState({
            ...uiState,
            theme: uiState.theme === 'light' ? 'dark' : 'light'
        })
    }, [uiState])

    return [fn]
}
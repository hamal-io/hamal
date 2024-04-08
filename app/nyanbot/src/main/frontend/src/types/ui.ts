export const UI_STATE_KEY = 'old-ui-state'

export type UiTheme = 'light' | 'dark'

export interface UiState {
    theme: UiTheme;
}
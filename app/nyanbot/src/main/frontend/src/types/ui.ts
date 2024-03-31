export const UI_STATE_KEY = 'ui-state'

export type UiTheme = 'light' | 'dark'

export interface UiState {
    theme: UiTheme;
    workspaceId?: string;
    namespaceId?: string;
}
export const UI_STATE_KEY = 'ui-state'

export type UiStateType = 'Unauthorized' | 'Authorized'

export interface UiState {
    type: UiStateType;
    theme: string;
}
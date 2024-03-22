export const UI_STATE_KEY = 'shared-state'

export type UiStateType = 'Unauthorized' | 'Authorized'

export interface UiState {
    type: UiStateType;
    theme: string;
}
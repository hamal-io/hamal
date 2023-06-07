import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

export interface ApiFunc {
    id: string;
    code: string;
}

export interface ApiListFuncResponse {
    funcs: ApiFunc[]
}

export const funcs: Writable<ApiFunc[]> = writable([]);
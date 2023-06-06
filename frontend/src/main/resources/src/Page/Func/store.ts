import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

export interface ApiFunc {
    id: bigint;
    code: string;
}

export const funcs: Writable<ApiFunc[]> = writable([]);
import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

export interface ApiExec {
    id: bigint;
    state: string;
}

export const execs: Writable<ApiExec[]> = writable([]);
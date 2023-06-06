import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

interface ApiExecution {
    id: bigint;
    state: string;
}

export const execs: Writable<ApiExecution[]> = writable([]);
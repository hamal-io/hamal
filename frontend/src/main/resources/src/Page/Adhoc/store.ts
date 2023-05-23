import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

interface ApiExecution {
    id: string;
    ref: string;
    state: string;
}

export const executions: Writable<ApiExecution[]> = writable([]);
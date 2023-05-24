import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

interface ApiFunction {
    id: string;
    code: string;
}

export const funcs: Writable<ApiFunction[]> = writable([]);
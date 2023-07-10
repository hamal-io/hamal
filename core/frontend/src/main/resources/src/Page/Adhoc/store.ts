import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

interface ApiAdhoc {
    id: string;
    state: string;
}

export const execs: Writable<ApiAdhoc[]> = writable([]);
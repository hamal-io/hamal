import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

interface ApiTrigger {
    id: string;
    code: string;
}

export const triggers: Writable<ApiTrigger[]> = writable([]);
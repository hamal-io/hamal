import type {Writable} from 'svelte/store';
import {writable} from 'svelte/store';

export interface ApiTrigger {
    id: bigint;
}

export const triggers: Writable<ApiTrigger[]> = writable([]);
import {HookListItem, TriggerListItem} from "@/types";

export interface HookWithTriggers {
    hook: HookListItem,
    trigger: Array<TriggerListItem>
}

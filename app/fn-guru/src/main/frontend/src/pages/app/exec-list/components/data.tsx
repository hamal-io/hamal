import {CheckCircledIcon, CrossCircledIcon,} from "@radix-ui/react-icons"

import {CircleDashed, PlayIcon} from "lucide-react";

export const statuses = [
    {
        value: "Planned",
        label: "Planned",
        icon: CircleDashed,
    },
    {
        value: "Scheduled",
        label: "Scheduled",
        icon: CircleDashed,
    },
    {
        value: "Queued",
        label: "Queued",
        icon: CircleDashed,
    },
    {
        value: "Started",
        label: "Started",
        icon: PlayIcon,
    },
    {
        value: "Completed",
        label: "Completed",
        icon: CheckCircledIcon,
    },
    {
        value: "Failed",
        label: "Failed",
        icon: CrossCircledIcon,
    },
]

export const invocations = [
    {
        value: "Adhoc",
        label: "Adhoc",
    },
    {
        value: "Endpoint",
        label: "Endpoint",
    },
    {
        value: "Event",
        label: "Event",
    },
    {
        value: "Func",
        label: "Func",
    },
    {
        value: "Hook",
        label: "Hook",
    },
    {
        value: "Schedule",
        label: "Schedule",
    },
]
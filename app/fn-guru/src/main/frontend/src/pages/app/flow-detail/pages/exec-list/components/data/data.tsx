import {
    CheckCircledIcon,
    CrossCircledIcon,
} from "@radix-ui/react-icons"

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


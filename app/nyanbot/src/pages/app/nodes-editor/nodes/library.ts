import {Waves} from "lucide-react";
import {NodeLibraryEntry} from "@/pages/app/nodes-editor/types.ts";

const NodesLibrary: Array<NodeLibraryEntry> = [
    {
        nodeId: "1",
        name: "Number Input",
        tags: "null",
        description: "Supplies Number",
        icon: Waves
    },
    {
        nodeId: "2",
        name: "Boolean",
        tags: "null",
        description: "Decide something",
        icon: Waves
    },
    {
        nodeId: "3",
        name: "Text Input",
        tags: "null",
        description: "Supplies Text",
        icon: Waves
    },
    {
        nodeId: "4",
        name: "Telegram",
        tags: "null",
        description: "Send a message via Telegram",
        icon: Waves
    }
]

export default NodesLibrary
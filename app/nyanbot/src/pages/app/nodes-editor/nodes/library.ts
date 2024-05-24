import {Waves} from "lucide-react";
import {NodeLibraryEntry} from "@/pages/app/nodes-editor/types.ts";

const NodesLibrary: Array<NodeLibraryEntry> = [
    {
        nodeId: "1",
        name: "Node One",
        tags: "null",
        description: "First awesome Node",
        icon: Waves
    },
    {
        nodeId: "2",
        name: "Node Two",
        tags: "null",
        description: "Second awesome Node",
        icon: Waves
    },
    {
        nodeId: "3",
        name: "Text Input",
        tags: "null",
        description: "Does something custom",
        icon: Waves
    },
    {
        nodeId: "4",
        name: "Telegram",
        tags: "null",
        description: "Does something custom",
        icon: Waves
    }
]

export default NodesLibrary
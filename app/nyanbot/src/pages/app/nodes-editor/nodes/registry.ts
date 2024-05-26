import {Node} from 'reactflow';
import TelegramSenderNode from "@/pages/app/nodes-editor/nodes/telegram-sender.tsx";
import TextInputNode from "@/pages/app/nodes-editor/nodes/text-input.tsx";
import NumberInputNode from "@/pages/app/nodes-editor/nodes/number-input.tsx";
import TextBuilderNode from "@/pages/app/nodes-editor/nodes/text-builder.tsx";
import FilterNode from "@/pages/app/nodes-editor/nodes/filter-node.tsx";

export const NodesRegistry: Node[] = [
    {
        id: '1',
        type: 'numberInput',
        position: {x: 0, y: 0},
        data: {},
    },
    {
        id: '2',
        type: 'filter',
        position: {x: 0, y: 0},
        data: {},
    },
    {
        id: '3',
        type: 'textInput',
        position: {x: 0, y: 0},
        data: {}
    },
    {
        id: '4',
        type: 'telegramSender',
        position: {x: 0, y: 0},
        data: {}
    },
    {
        id: '5',
        type: 'textBuilder',
        position: {x: 0, y: 0},
        data: {}
    }
];

export const nodeTypes = {
    textInput: TextInputNode,
    telegramSender: TelegramSenderNode,
    filter: FilterNode,
    numberInput: NumberInputNode,
    textBuilder: TextBuilderNode
};


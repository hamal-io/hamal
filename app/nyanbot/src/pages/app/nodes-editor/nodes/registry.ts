import {Node} from 'reactflow';
import TelegramSenderNode from "@/pages/app/nodes-editor/nodes/telegram-sender.tsx";
import TextInputNode from "@/pages/app/nodes-editor/nodes/text-input.tsx";
import BooleanNode from "@/pages/app/nodes-editor/nodes/boolean.tsx";

export const NodesRegistry: Node[] = [
    {
        id: '1',
        position: {x: 0, y: 0},
        data: {label: 'Hello'},
    },
    {
        id: '2',
        type: 'bool',
        position: {x: 0, y: 0},
        data: {label: 'World'},
    },
    {
        id: '3',
        type: 'textInput',
        position: {x: 0, y: 0},
        data: {value: 123}
    },
    {
        id: '4',
        type: 'telegramSender',
        position: {x: 0, y: 0},
        data: {value: 123}
    }

];

export const nodeTypes = {
    textInput: TextInputNode,
    telegramSender: TelegramSenderNode,
    bool: BooleanNode
};


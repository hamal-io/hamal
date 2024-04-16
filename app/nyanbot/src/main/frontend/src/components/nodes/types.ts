export type CanvasState = {
    scale: number;
    translate: Translate;
    position: Position;
    size: Size;
    rect: {
        left: number;
        right: number;
        top: number;
        bottom: number;
    };
    readonly: boolean;
}

export type ConnectionId = string

export type Connection = {
    id: ConnectionId;
    outputNode: {
        id: NodeId
    };
    outputPort: {
        id: PortId
    };
    inputNode: {
        id: NodeId
    };
    inputPort: {
        id: PortId
    };
}

export type ControlId = string
export type ControlType = 'Condition' | 'Input' | 'Init' | 'Invoke' | 'Text_Area'

export type Control = ControlCondition | ControlInput | ControlInit | ControlInvoke | ControlTextArea

type ControlBase = {
    id: ControlId;
    type: ControlType;
    nodeId: NodeId;
    label?: string;
}

export const isControl = (value: any): value is ControlBase => {
    return (
        typeof value === "object" &&
        value !== null &&
        "id" in value &&
        "type" in value
    );
}

export type ControlCondition = ControlBase & {
    type: "Condition";
}

export const isControlCondition = (value: any): value is ControlCondition => {
    return isControl(value) && value.type === 'Condition';
}

export type ControlInput = ControlBase & {
    type: 'Input';
}

export const isControlInput = (value: any): value is ControlCondition => {
    return isControl(value) && value.type === 'Input';
}

export type ControlTextArea = ControlBase & {
    type: 'Text_Area';
    value?: string;
    placeholder?: string;
    port: {
        id: PortId;
        inputType: string;
    }
}

export const isControlTextArea = (value: any): value is ControlTextArea => {
    return isControl(value) && value.type === 'Text_Area';
}

export type ControlInit = ControlBase & {
    type: 'Init';
    selector: string;
    description: string;
}

export const isControlInit = (value: any): value is ControlInit => {
    return isControl(value) && value.type === 'Init';
}

export type ControlInvoke = ControlBase & {
    type: "Invoke";
    port: {
        id: PortId;
    }
}

export const isControlInvoke = (value: any): value is ControlInvoke => {
    return isControl(value) && value.type === 'Invoke';
}


export type Graph = {
    nodes: Node[];
    connections: Connection[];
    controls: Control[];
}

export type NodeId = string
export type NodeType = string
export type NodeLabel = string

export type Node = {
    id: NodeId;
    type: NodeType;
    title?: NodeLabel;
    position: Position;
    size: Size;
    outputs: PortOutput[];
}

export type PortId = string

export type PortInput = {
    id: PortId;
    inputType: string; // FIXME type
}

export type PortOutput = {
    id: PortId;
    outputType: string; // FIXME type
}

export type Position = {
    x: number;
    y: number;
};


export interface SelectOption {
    label: string;
    value: string;
    description?: string;
    sortIndex?: number;
}

export type Size = {
    width: number;
    height: number;
};


export type Translate = {
    x: number;
    y: number;
};
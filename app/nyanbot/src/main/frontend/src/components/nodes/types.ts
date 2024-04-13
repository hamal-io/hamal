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
export type ControlType = 'Condition' | 'Input' | 'InputString'

export type Control = ControlCondition | ControlInput | ControlText

type ControlBase = {
    id: ControlId;
    type: ControlType;
    label?: string;
    ports: Port[];
}

export const isControl = (value: any): value is ControlBase => {
    return (
        typeof value === "object" &&
        value !== null &&
        "id" in value &&
        "type" in value &&
        "ports" in value
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

export type ControlText = ControlBase & {
    type: "InputString";
    text?: string;
    placeholder?: string;
}

export const isControlText = (value: any): value is ControlText => {
    return isControl(value) && value.type === 'InputString';
}

export type Graph = {
    nodes: Node[];
    connections: Connection[];
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
    controls: Control[]
    outputs: Port[]
}

export type PortId = string

export type Port = {
    id: PortId;
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
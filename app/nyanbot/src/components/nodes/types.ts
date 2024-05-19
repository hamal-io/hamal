// export type CanvasState = {
//     scale: number;
//     translate: Translate;
//     position: Position;
//     size: Size;
//     rect: {
//         left: number;
//         right: number;
//         top: number;
//         bottom: number;
//     };
//     readonly: boolean;
// }

export type ConnectionIndex = string

export type Connection = {
    id: ConnectionIndex;
    outputNode: {
        id: NodeIndex
    };
    outputPort: {
        id: PortIndex
    };
    inputNode: {
        id: NodeIndex
    };
    inputPort: {
        id: PortIndex
    };
}

export type ControlId = string
export type ControlType = 'Condition' | 'Input' | 'Input_Boolean' | 'Invoke' | 'Input_String'

export type Control =
    ControlCondition
    | ControlInput
    | ControlInputBoolean
    | ControlInvoke
    | ControlInputString

type ControlBase = {
    id: ControlId;
    type: ControlType;
    NodeIndex: NodeIndex;
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

type ControlWithPort = {
    id: ControlId;
    port: {
        id: PortIndex;
    }
}

export const isControlWithPort = (value: any): value is ControlWithPort => {
    return isControl(value) && "port" in value;
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

export type ControlInputString = ControlBase & {
    type: 'Input_String';
    value?: string;
    placeholder?: string;
    port: {
        id: PortIndex;
        // type: string;
    }
}

export const isControlInputString = (value: any): value is ControlInputString => {
    return isControl(value) && value.type === 'Input_String';
}

export type ControlInvoke = ControlBase & {
    type: "Invoke";
    port: {
        id: PortIndex;
    }
}

export const isControlInvoke = (value: any): value is ControlInvoke => {
    return isControl(value) && value.type === 'Invoke';
}

export type ControlInputBoolean = ControlBase & {
    type: "Input_Boolean";
    value: boolean;
}

export const isControlInputBoolean = (value: unknown): value is ControlInputBoolean => {
    return isControl(value) && value.type === 'Input_Boolean';
}


export type Graph = {
    nodes: Node[];
    connections: Connection[];
    controls: Control[];
}

export type NodeIndex = string
export type NodeType = string
export type NodeLabel = string

export type Node = {
    id: NodeIndex;
    type: NodeType;
    title?: NodeLabel;
    position: Position;
    size: Size;
    outputs: Port[];
}

export type PortIndex = string

export type Port = {
    id: PortIndex;
}


export type Position = {
    x: number;
    y: number;
};

export type Rect = {
    left: number;
    right: number;
    top: number;
    bottom: number;
}


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
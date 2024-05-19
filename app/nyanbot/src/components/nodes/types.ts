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

export type ConnectionIndex = number

export type Connection = {
    index: ConnectionIndex;
    outputNode: {
        index: NodeIndex
    };
    outputPort: {
        index: PortIndex
    };
    inputNode: {
        index: NodeIndex
    };
    inputPort: {
        index: PortIndex
    };
}

export type ControlIndex = number
export type ControlType = 'Input_Boolean' | 'Input_String'

export type Control =
    | ControlInputBoolean
    | ControlInputString

type ControlBase = {
    index: ControlIndex;
    type: ControlType;
    nodeIndex: NodeIndex;
    label?: string;
}

export const isControl = (value: any): value is ControlBase => {
    return (
        typeof value === "object" &&
        value !== null &&
        "index" in value &&
        "type" in value
    );
}

type ControlWithPort = ControlBase & {
    port: {
        index: PortIndex;
        form: string;
    }
}

export const isControlWithPort = (value: any): value is ControlWithPort => {
    return isControl(value) && "port" in value;
}

export type ControlInputBoolean = ControlWithPort & {
    type: "Input_Boolean";
    value?: boolean;
}

export const isControlInputBoolean = (value: unknown): value is ControlInputBoolean => {
    return isControl(value) && value.type === 'Input_Boolean';
}


export type ControlInputString = ControlWithPort & {
    type: 'Input_String';
    value?: string;
    placeholder?: string;
}

export const isControlInputString = (value: any): value is ControlInputString => {
    return isControl(value) && value.type === 'Input_String';
}


export type Graph = {
    nodes: Node[];
    connections: Connection[];
    controls: Control[];
}

export type NodeIndex = number
export type NodeType = string
export type NodeLabel = string

export type Node = {
    index: NodeIndex;
    type: NodeType;
    title?: NodeLabel;
    position: Position;
    size: Size;
    outputs: Port[];
}

export type PortIndex = number

export type Port = {
    index: PortIndex;
    form: string;
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
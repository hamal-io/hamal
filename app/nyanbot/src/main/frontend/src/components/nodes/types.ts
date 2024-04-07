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
    inputNodeId: NodeId;
    outputNodeId: NodeId;
}

export type ControlId = string
export type ControlType = string

export type Control = {
    id: ControlId;
    type: ControlType;
}

export type NodeId = string
export type NodeType = string

export type Node = {
    id: NodeId;
    type: NodeType;
    position: Position;
    size: Size;
    controls: Control[]
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
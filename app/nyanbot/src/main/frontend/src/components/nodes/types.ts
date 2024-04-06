export type NodeId = string
export type NodeType = string

export type ConnectionId = string

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

export type Translate = {
    x: number;
    y: number;
};

export type Position = {
    x: number;
    y: number;
};

export type Size = {
    width: number;
    height: number;
};

export type Connection = {
    id: ConnectionId;
    inputNodeId: NodeId;
    outputNodeId: NodeId;
}

export type Node = {
    id: NodeId;
    type: NodeType;
    position: Position;
    size: Size;
}
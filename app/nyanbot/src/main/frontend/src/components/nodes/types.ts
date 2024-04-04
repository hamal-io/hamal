export type NodeId = string


export type CanvasState = {
    scale: number;
    position: Position;
    size: Size;
    rect: {
        left: number;
        right: number;
        top: number;
        bottom: number;
    }
}

export type Position = {
    x: number;
    y: number;
};

export type Size = {
    width: number;
    height: number;
};
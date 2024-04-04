import React, {createContext, MutableRefObject} from "react";
import {CanvasState} from "./types.ts";

export const ContextCanvasState = createContext<CanvasState>({
    scale: 1,
    position: {x: 0, y: 0}
});


export const ContextCanvasRect = createContext<MutableRefObject<DOMRect> | null>(null)

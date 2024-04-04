import React from "react";
import {CanvasState} from "./types.ts";

export const ContextCanvas = React.createContext<CanvasState>({
    scale: 1,
    position: {x: 0, y: 0}
});
import {createContext} from "react";
import {CanvasState} from "./types.ts";

export const ContextCanvasState = createContext<CanvasState>({
    scale: 1,
    position: {x: 0, y: 0},
    size: {width: 0, height: 0},
    rect: {left: 0, right: 0, top: 0, bottom: 0}
});



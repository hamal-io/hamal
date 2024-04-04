import React, {FC, useCallback, useEffect, useRef, useState} from "react";
import {Draggable} from './draggable.tsx';
import styles from './canvas.module.css'
import {ContextCanvasState} from "@/components/nodes/context.ts";
import {CanvasState} from "./types.ts";

type CanvasProps = {
    children: React.ReactNode;
}

export const Canvas: FC<CanvasProps> = ({ children}) => {

    const [canvasState, setCanvasState] = useState<CanvasState>({
        scale: 1,
        translate: {x: 0, y: 0},
        position: {x: 0, y: 0},
        size: {width: 0, height: 0},
        rect: {
            left: 0,
            right: 0,
            top: 0,
            bottom: 0,
        }
    })

    const scale = 1;

    const wrapper = useRef<HTMLDivElement>(null);
    const translateWrapper = useRef<HTMLDivElement>(null);

    const setCanvasRect = useCallback(() => {
        if (wrapper.current) {
            const {x, y, left, right, top, bottom, width, height} = wrapper.current.getBoundingClientRect();

            setCanvasState({
                ...canvasState,
                position: {x, y},
                size: {width, height},
                rect: {
                    left,
                    right,
                    top,
                    bottom,
                }
            })
        }
    }, []);

    useEffect(() => {
        if (wrapper.current) {
            const {x, y, left, right, top, bottom, width, height} = wrapper.current.getBoundingClientRect();
            setCanvasState({
                ...canvasState,
                position: {x, y},
                size: {width, height},
                rect: {
                    left,
                    right,
                    top,
                    bottom,
                }
            })
        }

        window.addEventListener("resize", setCanvasRect);
        return () => {
            window.removeEventListener("resize", setCanvasRect);
        };
    }, [setCanvasRect]);


    return (
        <ContextCanvasState.Provider value={canvasState}>
            <Draggable
                id="CANVAS_1"
                data-component="canvas"
                className={styles.wrapper}
                innerRef={wrapper}
                // onContextMenu={handleContextMenu}
                // onMouseEnter={handleMouseEnter}
                // onDragDelayStart={handleDragDelayStart}
                // onDragStart={handleDragStart}
                // onDrag={handleMouseDrag}
                // onDragEnd={handleDragEnd}
                // onKeyDown={handleKeyDown}
                tabIndex={-1}
                // style={{ cursor: "grab" }}
                // disabled={disablePan || (spaceToPan && !spaceIsPressed)}
            >
                <div
                    ref={translateWrapper}
                    className={styles.transformWrapper}
                    // style={{transform: `translate(${canvasState.translate.x}px, ${canvasState.translate.y}px)`}}
                >
                    <div
                        className={styles.scaleWrapper}
                        style={{transform: `scale(${scale})`}}
                    >
                        {children}
                    </div>
                </div>
            </Draggable>
        </ContextCanvasState.Provider>
    )

}
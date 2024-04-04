import React, {FC, useCallback, useEffect, useRef} from "react";
import {Draggable} from './draggable.tsx';
import styles from './canvas.module.css'
import {Position} from "@/components/nodes/types.ts";
import {Node} from "@/components/nodes/node.tsx";
import {ContextCanvasRect, ContextCanvasState} from "@/components/nodes/context.ts";

type CanvasProps = {
    position: Position;
    children: React.ReactNode;
}

export const Canvas: FC<CanvasProps> = ({
                                            position,
                                            children
                                        }) => {
    const scale = 1;

    const wrapper = useRef<HTMLDivElement>(null);
    const translateWrapper = useRef<HTMLDivElement>(null);

    const canvasRect = useRef<DOMRect>();
    const setCanvasRect = useCallback(() => {
        if (wrapper.current) {
            canvasRect.current = wrapper.current.getBoundingClientRect();
        }
    }, []);

    useEffect(() => {
        if (wrapper.current) {
            canvasRect.current = wrapper.current.getBoundingClientRect();
        }

        window.addEventListener("resize", setCanvasRect);
        return () => {
            window.removeEventListener("resize", setCanvasRect);
        };
    }, [canvasRect, setCanvasRect]);


    return (
        <ContextCanvasRect.Provider value={canvasRect}>
            <ContextCanvasState.Provider value={{scale: 1, position: {x: 0, y: 0}}}>
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
                    // tabIndex={-1}
                    // stageState={{ scale, translate }}
                    // style={{ cursor: "grab" }}
                    // disabled={disablePan || (spaceToPan && !spaceIsPressed)}
                >
                    <div
                        ref={translateWrapper}
                        className={styles.transformWrapper}
                        style={{transform: `translate(${-position.x}px, ${-position.y}px)`}}
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
        </ContextCanvasRect.Provider>
    )

}
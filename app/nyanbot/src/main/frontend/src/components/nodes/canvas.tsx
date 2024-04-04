import React, {FC, useCallback, useEffect, useRef} from "react";
import {Draggable} from './draggable.tsx';
import styles from './canvas.module.css'
import {Position} from "@/components/nodes/types.ts";
import {Node} from "@/components/nodes/node.tsx";

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
            canvasState={{position, scale}}
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
                    <Node
                        id='1'
                        type="test"
                        position={{x: 0, y: 0}}
                        size={{width: 250, height: 100}}
                        canvasRect={canvasRect}
                        onDragStart={() => console.log("start dragging")}
                    />
                    {children}
                </div>
            </div>
        </Draggable>

    )
}
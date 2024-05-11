import React, {FC, useCallback, useContext, useEffect, useRef} from "react";
import {Draggable} from './draggable.tsx';
import styles from './canvas.module.css'
import {ConnectionListWidget} from "@/components/nodes/connection.tsx";
import {NodeWidget} from "@/components/nodes/node.tsx";
import {Connection, Node} from "@/components/nodes/types.ts";
import {ContextEditorState} from "@/components/nodes/editor.tsx";

type CanvasProps = {
    nodes: Node[];
    connections: Connection[];
    readonly: boolean;
}

export const Canvas: FC<CanvasProps> = ({
                                            nodes, connections, readonly
                                        }) => {
    const {state, dispatch} = useContext(ContextEditorState);

    const scale = 1;

    const wrapper = useRef<HTMLDivElement>(null);
    const translateWrapper = useRef<HTMLDivElement>(null);

    const setCanvasRect = useCallback(() => {
        if (wrapper.current) {
            const {x, y, left, right, top, bottom, width, height} = wrapper.current.getBoundingClientRect();

            dispatch({
                type: "CANVAS_SET",
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

            dispatch({
                type: "CANVAS_SET",
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
        // <ContextCanvasState.Provider value={canvasState}>
        <div className={`h-screen`}>

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

                        {nodes.map(node => (
                            <NodeWidget
                                key={node.id}
                                node={node}
                                onDragStart={() => console.log("start dragging")}
                            />
                        ))}

                    </div>
                </div>

                <ConnectionListWidget/>

            </Draggable>
        </div>
    )

}
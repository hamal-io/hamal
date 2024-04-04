import React, {FC, RefObject, useContext} from "react";
import {NodeId, Position, Size} from './types.ts';
import {Draggable} from './draggable.tsx';
import styles from "@/components/nodes/node.module.css";
import {ContextCanvasState} from "@/components/nodes/context.ts";
import {Ports} from "@/components/nodes/port.tsx";

type NodeProps = {
    id: NodeId;
    position: Position;
    size: Size;
    type: string;
    // canvasRect: RefObject<DOMRect | undefined>;
    onDragStart: () => void;
}

export const Node: FC<NodeProps> = ({
    type,
    position,
    size,
    // canvasRect
}) => {

    const wrapper = React.useRef<HTMLDivElement>(null);

    const startDrag = (e: React.MouseEvent | React.TouchEvent) => {
        console.log("start drag",)
        // onDragStart();
    };

    const handleDrag = ({x, y}: Position) => {
        if (wrapper.current) {
            wrapper.current.style.transform = `translate(${x}px,${y}px)`;
        }
    };

    const handleDragEnd = (e: MouseEvent, {x, y}) => {
        // dispatch({type: CommentActionTypes.SET_COMMENT_COORDINATES, id, x, y});
    };

    const isEditing = false
    const width = size.width;
    const height = size.height;

    // FIXME
    return (
        <Draggable
            innerRef={wrapper}
            className={styles.wrapper}
            style={{
                transform: `translate(${position.x}px,${position.y}px)`,
                width,
                height,
                zIndex: isEditing ? 999 : ""
            }}
            onDragStart={startDrag}
            onDrag={handleDrag}
            onDragEnd={handleDragEnd}
            // onContextMenu={handleContextMenu}
            // onDoubleClick={startTextEdit}
            // onWheel={e => e.stopPropagation()}
            data-color={"green"}
            data-component="node"

        >
            <Ports
                type='test'
            />

            {/*<Draggable*/}
            {/*    // className={styles.resizeThumb}*/}
            {/*    // stageState={stageState}*/}
            {/*    // stageRect={stageRect}*/}
            {/*    // onDrag={handleResize}*/}
            {/*    // onDragEnd={handleResizeEnd}*/}
            {/*    data-component="node-resize-handle"*/}
            {/*/>*/}
        </Draggable>
    )
}
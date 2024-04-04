import React, {FC, RefObject, useState} from "react";
import {NodeId, Position, Size} from './types.ts';
import {Ports} from './port.tsx'
import {Draggable} from './draggable.tsx';
import styles from "@/components/nodes/node.module.css";

type NodeProps = {
    id: NodeId;
    position: Position;
    size: Size;
    type: string;
    objectRef: RefObject<DOMRect | undefined>;
    onDragStart: () => void;
}

export const Node : FC<NodeProps> = ({
   type,
   position,
   size
}) => {

    const wrapper = React.useRef<HTMLDivElement>(null);
    const textarea = React.useRef<HTMLTextAreaElement>(null);


    const startDrag = (e: React.MouseEvent | React.TouchEvent) => {
        console.log("start drag")
        // onDragStart();
    };

    const handleDrag = ({ x, y }: Position) => {
        console.log("handle drag")
        if (wrapper.current) {
            wrapper.current.style.transform = `translate(${x}px,${y}px)`;
        }
    };

    const handleDragEnd = (e: MouseEvent, { x, y }) => {
        // dispatch({type: CommentActionTypes.SET_COMMENT_COORDINATES, id, x, y});
    };

    const isEditing = false
    const width = size.width;
    const height = size.height;

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
            // stageState={stageState}
            // stageRect={stageRect}
            onDragStart={startDrag}
            onDrag={handleDrag}
            onDragEnd={handleDragEnd}
            // onContextMenu={handleContextMenu}
            // onDoubleClick={startTextEdit}
            // onWheel={e => e.stopPropagation()}
            data-color={"green"}
            data-nodes-component="comment"
        >
            <Ports
                type='test'
            />

            <Draggable
                // className={styles.resizeThumb}
                // stageState={stageState}
                // stageRect={stageRect}
                // onDrag={handleResize}
                // onDragEnd={handleResizeEnd}
                data-nodes-component="comment-resize-handle"
            />
        </Draggable>
    )
}
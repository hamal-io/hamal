import React, {FC, RefObject, useState} from "react";
import {Position, Size} from './types.ts';
import {Draggable} from './draggable.tsx';
import styles from "./comment.module.css";

type CommentProps = {
    id: string;
    position: Position;
    size: Size;
    text: string;
    objectRef: RefObject<DOMRect | undefined>;
    onDragStart: () => void;
}

export const Comment : FC<CommentProps> = ({
    text,
    position,
    size
}) => {
    const [txt, setTxt] = useState(text)
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
            data-color={"red"}
            data-nodes-component="comment"
        >
                    <textarea
                        data-nodes-component="comment-textarea"
                        // className={styles.textarea}
                        // onChange={handleTextChange}
                        onMouseDown={e => e.stopPropagation()}
                        // onBlur={endTextEdit}
                        placeholder="Text of the comment..."
                        autoFocus
                        value={txt}
                        onChange={evt => setTxt(evt.target.value)}
                        ref={textarea}
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
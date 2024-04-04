import React, {FC} from "react";
import {Draggable} from './draggable.tsx';
import styles from './canvas.module.css'
import {Position} from "@/components/nodes/types.ts";

type CanvasProps = {
    position: Position;
    children: React.ReactNode;
}

export const Canvas: FC<CanvasProps> = ({
    position,
    children
}) => {
    const scale = 1;
    const translateWrapper = React.useRef<HTMLDivElement>(null);

    return (

        <Draggable
            data-component="canvas"
            // id={`${STAGE_ID}${editorId}`}
            className={styles.wrapper}
            // innerRef={wrapper}
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
            // data-flume-stage={true}
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

    )
}
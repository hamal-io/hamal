import React, {FC} from "react";
import {NodeId, NodeType, Position, Size} from './types.ts';
import {Draggable} from './draggable.tsx';
import styles from "@/components/nodes/node.module.css";
import {Ports} from "@/components/nodes/port.tsx";
import {Controls} from "@/components/nodes/control.tsx";

type NodeProps = {
    id: NodeId;
    position: Position;
    size: Size;
    type: NodeType;
    // canvasRect: RefObject<DOMRect | undefined>;
    onDragStart: () => void;
}

export const Node: FC<NodeProps> = ({
                                        type,
                                        position,
                                        size,
                                        // canvasRect
                                    }) => {

    const nodeWrapper = React.useRef<HTMLDivElement>(null);


    const startDrag = (e: React.MouseEvent | React.TouchEvent) => {
        console.log("start drag",)
        // onDragStart();
    };

    const handleDrag = ({x, y}: Position) => {
        if (nodeWrapper.current) {
            nodeWrapper.current.style.transform = `translate(${x}px,${y}px)`;
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
        // <Draggable
        //     innerRef={wrapper}
        //     className={styles.wrapper}
        //     style={{
        //         transform: `translate(${position.x}px,${position.y}px)`,
        //         width,
        //         height,
        //         zIndex: isEditing ? 999 : ""
        //     }}
        //     onDragStart={startDrag}
        //     onDrag={handleDrag}
        //     onDragEnd={handleDragEnd}
        //     // onContextMenu={handleContextMenu}
        //     // onDoubleClick={startTextEdit}
        //     // onWheel={e => e.stopPropagation()}
        //     data-color={"green"}
        //     data-component="node"
        //
        // >
        //     <Ports
        //         type='test'
        //     />
        //
        //     {/*<Draggable*/}
        //     {/*    // className={styles.resizeThumb}*/}
        //     {/*    // stageState={stageState}*/}
        //     {/*    // stageRect={stageRect}*/}
        //     {/*    // onDrag={handleResize}*/}
        //     {/*    // onDragEnd={handleResizeEnd}*/}
        //     {/*    data-component="node-resize-handle"*/}
        //     {/*/>*/}
        // </Draggable>


        <Draggable
            className={styles.wrapper}
            style={{
                width,
                height,
                transform: `translate(${position.x}px, ${position.y}px)`
            }}
            onDragStart={startDrag}
            onDrag={handleDrag}
            // onDragEnd={stopDrag}
            innerRef={nodeWrapper}
            // data-node-id={id}
            data-component="node"
            // data-node-type={currentNodeType.type}
            // data-component-is-root={!!root}
            // onContextMenu={handleContextMenu}
            // stageState={stageState}
            // stageRect={stageRect}
        >


            <Controls />

            <Ports
                // nodeId={id}
                // inputs={inputs}
                // outputs={outputs}
                // connections={connections}
                // updateNodeConnections={updateNodeConnections}
                // inputData={inputData}
            />




            {/*<Control type={"number"}/>*/}


            {/*{type ==='INIT' && initNode()}*/}
            {/*{type ==='FILTER' && filterNode()}*/}
            {/*{type ==='TELEGRAM_SEND_MESSAGE' && telegramSendNode()}*/}

        </Draggable>
    )
}

// const initNode = () => {
//     return (
//         <span style={{color: "black"}}>On new LP</span>
//     )
// }
//
// const filterNode = () => {
//     return (
//         <span style={{color: "black"}}>Filter</span>
//     )
// }
//
//
// const telegramSendNode = () => {
//     return (
//         <div>
//             <span style={{color: "black"}}>Send telegram message</span>
//         </div>
//     )
// }
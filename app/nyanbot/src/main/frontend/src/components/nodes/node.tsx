import React, {FC} from "react";
import {Node, Position} from './types.ts';
import {Draggable} from './draggable.tsx';
import styles from "@/components/nodes/node.module.css";
import {PortOutputWidget} from "@/components/nodes/port.tsx";
import {ControlListWidget} from "@/components/nodes/control.tsx";

type NodeWidgetProps = {
    // id: NodeId;
    // position: Position;
    // size: Size;
    // type: NodeType;
    node: Node;
    onDragStart: () => void;

}

export const NodeWidget: FC<NodeWidgetProps> = ({node}) => {

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
    const width = node.size.width;
    const height = node.size.height;

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
                transform: `translate(${node.position.x}px, ${node.position.y}px)`
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

            <span className='text-teal-200'>{node.title} </span>


            <ControlListWidget node={node}/>

            {node.outputs.length === 1 && (
                <PortOutputWidget
                    port={node.outputs[0]}
                    // nodeId={id}
                    // inputs={inputs}
                    // outputs={outputs}
                    // connections={connections}
                    // updateNodeConnections={updateNodeConnections}
                    // inputData={inputData}
                />)
            }


            {/*<Control type={"number"}/>*/}


        </Draggable>
    )
}
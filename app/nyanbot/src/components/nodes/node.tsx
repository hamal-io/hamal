import React, {FC, useContext} from "react";
import {Node, Position} from './types.ts';
import {Draggable} from './draggable.tsx';
import styles from "@/components/nodes/node.module.css";
import {PortInputWidget, PortOutputWidget} from "@/components/nodes/port.tsx";
import {ControlListWidget} from "@/components/nodes/control.tsx";
import {ContextEditorState} from "@/components/nodes/editor.tsx";

type NodeWidgetProps = {
    // id: NodeIndex;
    // position: Position;
    // size: Size;
    // type: NodeType;
    node: Node;
    // onDragStart: () => void;

}

export const NodeWidget: FC<NodeWidgetProps> = ({node}) => {
    const {dispatch} = useContext(ContextEditorState)
    const nodeWrapper = React.useRef<HTMLDivElement>(null);

    const startDrag = () => {
        console.debug("Drag started")
        dispatch({type: "NODE_SELECTED", index: node.index})
        // onDragStart();
    };

    const handleDrag = ({x, y}: Position) => {
        if (nodeWrapper.current) {
            nodeWrapper.current.style.transform = `translate(${x}px,${y}px)`;

            dispatch({type: "NODE_POSITION_UPDATED", position: {x, y}});
        }
    };

    const handleDragEnd = (e: MouseEvent, {x, y}) => {
        console.debug("Drag ended")
        dispatch({type: "NODE_POSITION_UPDATED", position: {x, y}});
        dispatch({type: "NODE_UNSELECTED"})
    };

    // const isEditing = false
    const width = node.size.width;
    const height = node.size.height;

    const stateNode = useContext(ContextEditorState).state.nodes[node.index]

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
            onDragEnd={handleDragEnd}
            innerRef={nodeWrapper}
            data-node-index={node.index}
            data-component="node"
            // data-node-type={currentNodeType.type}
            // data-component-is-root={!!root}
            // onContextMenu={handleContextMenu}
            // stageState={stageState}
            // stageRect={stageRect}
        >

            <span className='text-teal-200'>{node.title} </span>
            {/*{stateNode.inputs.length === 1 && (*/}
            {/*    <PortInputWidget port={stateNode.inputs[0]}/>*/}
            {/*)}*/}


            <ControlListWidget node={node}/>

            {stateNode.outputs.map(port =>
                (<PortOutputWidget
                    port={port}
                    // NodeIndex={id}
                    // inputs={inputs}
                    // outputs={outputs}
                    // connections={connections}
                    // updateNodeConnections={updateNodeConnections}
                    // inputData={inputData}
                />)
            )
            }


            {/*<Control type={"number"}/>*/}


        </Draggable>
    )
}
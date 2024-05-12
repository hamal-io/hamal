import React, {FC, useContext, useState} from "react";
import styles from "./menu.module.css";
import {Button} from "@/components/ui/button.tsx";
import {Card, CardContent, CardTitle} from "@/components/ui/card.tsx";
import {ContextEditorState} from "@/components/nodes/editor.tsx";

type MenuProps = {}

export const Menu: FC<MenuProps> = ({}) => {
    const [showLibrary, setShowLibrary] = useState(false)
    return (
        <div>
            <div className={`flex flex-col ${styles.menu}`}>
                <Button onClick={_ => setShowLibrary(true)}> Add Node </Button>
            </div>
            {showLibrary && <NodeLibrary onClose={() => setShowLibrary(false)}/>}
        </div>
    )
}

type NodeLibraryProps = {
    onClose: () => void;
}

const NodeLibrary: FC<NodeLibraryProps> = ({onClose}) => {

    const {dispatch} = useContext(ContextEditorState)

    return (
        <Card className={`p-4 w-1/6 ${styles.library}`}>
            <CardTitle>
                <div className="flex flex-row items-center">
                    <h1>Node Library</h1>
                    <Button onClick={onClose}> Close </Button>
                </div>
            </CardTitle>
            <CardContent>
                <div className={"flex flex-col"}>

                    <Button onClick={() => dispatch({type: "NODE_ADDED"})}> New Node </Button>

                    {/*<Draggable*/}
                    {/*    className={styles.wrapper}*/}
                    {/*    style={{*/}
                    {/*        width: 100,*/}
                    {/*        height: 100,*/}
                    {/*        transform: `translate(${node.position.x}px, ${node.position.y}px)`*/}
                    {/*    }}*/}
                    {/*    onDragStart={startDrag}*/}
                    {/*    onDrag={handleDrag}*/}
                    {/*    onDragEnd={handleDragEnd}*/}
                    {/*    innerRef={nodeWrapper}*/}
                    {/*    // data-node-id={id}*/}
                    {/*    // data-component="node"*/}
                    {/*    // data-node-type={currentNodeType.type}*/}
                    {/*    // data-component-is-root={!!root}*/}
                    {/*    // onContextMenu={handleContextMenu}*/}
                    {/*    // stageState={stageState}*/}
                    {/*    // stageRect={stageRect}*/}
                    {/*>*/}

                    {/*    <span className='text-teal-200'> test </span>*/}

                    {/*</Draggable>*/}

                </div>
            </CardContent>
        </Card>
    );
}
import React, {FC, useContext, useState} from "react";
import styles from "./menu.module.css";
import {Button} from "@/components/ui/button.tsx";
import {Card, CardContent, CardTitle} from "@/components/ui/card.tsx";
import {ContextEditorState} from "@/components/nodes/editor.tsx";
import {Draggable} from "@/components/nodes/draggable.tsx";
import {Graph, Position} from "@/components/nodes/types.ts";

type MenuProps = {
    onTest: () => void;
}

export const Menu: FC<MenuProps> = ({onTest}) => {
    const {state} = useContext(ContextEditorState);
    const [showLibrary, setShowLibrary] = useState(true)
    return (
        <div>
            <div className={`flex flex-col ${styles.menu}`}>
                <Button onClick={_ => setShowLibrary(true)}> Add Node </Button>
            </div>
            <div className={styles.execute}>
                <Button onClick={() => {
                    onTest()
                }}> Run </Button>
            </div>
            {showLibrary && <NodeLibrary onClose={() => setShowLibrary(false)}/>}
        </div>
    )
}

type NodeLibraryProps = {
    onClose: () => void;
}

const NodeLibrary: FC<NodeLibraryProps> = ({onClose}) => {
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
                    {[
                        {type: "Init"},
                        {type: "Telegram_Send_Message"},
                        {type: "Filter"},
                        {type: "Input"}
                    ].map((prototype, index) =>
                        <MenuItem {...prototype} index={index}/>
                    )}

                </div>
            </CardContent>
        </Card>
    );
}

type MenuItemProps = {
    index: number;
    type: string
}

const MenuItem: FC<MenuItemProps> = ({type, index}) => {
    const {state: {canvas}, dispatch} = useContext(ContextEditorState)

    const nodeWrapper = React.useRef<HTMLDivElement>(null);

    let originalPosition = null;

    const startDrag = (e: React.MouseEvent) => {
        console.debug("Drag started")
        originalPosition = nodeWrapper.current.style.transform
    };

    const byScale = (value: number) => (1 / canvas.scale) * value;

    const getScaledPosition = (position: Position): Position => {

        const offsetX = (canvas.size.width) / 2;
        const offsetY = (canvas.size.height) / 2;

        return {
            x: byScale(position.x - (canvas.rect.left) + offsetX) + byScale(canvas.translate.x),
            y: byScale(position.y - (canvas.rect.top) + offsetY) + byScale(canvas.translate.y)
        };
    };

    const handleDrag = (position: Position) => {
        if (nodeWrapper.current) {
            const scaledPosition = getScaledPosition(position)
            nodeWrapper.current.style.transform = `translate(${scaledPosition.x}px,${scaledPosition.y}px)`;
        }
    };

    const handleDragEnd = (e: MouseEvent, position: Position) => {
        console.debug("Drag ended")

        dispatch({type: "NODE_ADDED", nodeType: type, position})
        nodeWrapper.current.style.transform = originalPosition;
    };

    // const isEditing = false
    // const width = 100;
    // const height = 100;

    // const position = {x: 0, y: 0}

    return (
        <Draggable
            className={styles.wrapper}
            style={{
                width: 100,
                height: 100,
                transform: `translate(0px, ${index * 100 + 100}px)`
            }}
            onDragStart={startDrag}
            onDrag={handleDrag}
            onDragEnd={handleDragEnd}
            innerRef={nodeWrapper}
        >

            <span className='text-teal-200'> {type} </span>

        </Draggable>
    )
}
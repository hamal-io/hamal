import {Handle, NodeProps, Position} from "reactflow";
import handle from "@/pages/app/nodes-editor/nodes/handle.module.css";
import node from "@/pages/app/nodes-editor/nodes/node.module.css";
import {useCallback} from "react";

export default function TextBuilderNode(props: NodeProps) {
    const onChange = useCallback((evt) => {
        console.log(evt.target.value); // Handle user input here
    }, []);


    return (
        <>
            <div className={node.node}>
                <div className={node.nodeHeader}>To string:</div>
                <div>
                    <Handle type="source" position={Position.Right} id="string" className={`
                            ${handle.text}
                            `}
                    />
                </div>
            </div>
            <Handle type="target" position={Position.Left} id="union" className={`
                            ${handle.union}
            `}
            />

        </>
    )
}
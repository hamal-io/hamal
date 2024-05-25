import {useCallback} from 'react';
import {Handle, NodeProps, Position} from 'reactflow';
import handle from "@/pages/app/nodes-editor/nodes/handle.module.css";
import node from "@/pages/app/nodes-editor/nodes/node.module.css";

function TextInputNode() {
    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);

    return (
        <>
            <div className={node.node}>
                <div className={node.nodeHeader}>Text Input:</div>
                <div>
                    <input id="text" name="text" className="nodrag border"/>
                    <Handle type="source" position={Position.Right} id="string" className={`
                            ${handle.text}
                            `}
                    />
                </div>
            </div>

        </>
    );
}

export default TextInputNode
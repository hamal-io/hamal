import {useCallback} from 'react';
import {Handle, NodeProps, Position} from 'reactflow';
import handle from "@/pages/app/nodes-editor/nodes/handle.module.css";
import node from "@/pages/app/nodes-editor/nodes/node.module.css";

function NumberInputNode(props: NodeProps) {
    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);

    return (
        <>
            <div className={node.node}>
                <div className={node.nodeHeader}>Number Input:</div>
                <div>
                    <input id="text" name="number" className="nodrag border"/>
                    <Handle type="source" position={Position.Right} id="number" className={`
                            ${handle.number}
                    `}
                    />
                </div>
            </div>

        </>
    );
}

export default NumberInputNode
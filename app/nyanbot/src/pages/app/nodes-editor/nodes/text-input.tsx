import {useCallback} from 'react';
import {Handle, NodeProps, Position} from 'reactflow';
import styles from "@/pages/app/nodes-editor/nodes/handle.module.css";

function TextInputNode(props: NodeProps) {
    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);

    return (
        <>
            <div className={"flex flex-col border-2 rounded-lg"}>
                <div>Text Input:</div>
                <input id="text" name="text" onChange={onChange} className="nodrag border"/>
            </div>
            <Handle type="source" position={Position.Right} id="false" className={styles.text}/>
        </>
    );
}

export default TextInputNode
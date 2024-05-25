import {Handle, NodeProps, Position} from "reactflow";
import {useCallback} from "react";
import styles from "./handle.module.css";

export default function BooleanNode(props: NodeProps) {
    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);
    return (
        <>
            <div className={"flex flex-col p-4 border-2 rounded-lg"}>
                <div className={"flex flex-row items-center justify-between"}>
                    <div>True</div>
                    <input type="checkbox"/>
                    <Handle type="source" position={Position.Right} id="false"
                            className={`${styles.boolean} ${styles.right}`}/>
                </div>
                <div className={"flex flex-row items-center justify-between"}>
                    <div>False</div>
                    <input type="checkbox"/>
                    <Handle type="source" position={Position.Right} id="false"
                            className={`${styles.boolean} ${styles.right}`}/>

                </div>
            </div>
            <Handle type="target" position={Position.Left} id="true" className={styles.boolean}/>

        </>
    );
}
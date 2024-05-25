import {Handle, NodeProps, Position} from 'reactflow';
import styles from "@/pages/app/nodes-editor/nodes/handle.module.css";


export default function TelegramSenderNode(props: NodeProps) {
    return (
        <>
            <div className={"flex flex-col border-2 rounded-lg"}>
                <div>Notify Telegram</div>
                <div>
                    <Handle type="target" position={Position.Left} id="false" className={`
                    ${styles.text} 
                    ${styles.left}`
                    }/>
                    <input placeholder={"Message"} id="text" name="text" className="nodrag border"/>
                </div>
                <div>
                    <Handle type="target" position={Position.Left} id="false" className={`
                    ${styles.number} 
                    ${styles.left}`
                    }/>
                    <input placeholder={"Telegram Number"} id="text" name="text" className="nodrag border"/>
                </div>

            </div>
        </>
    );
}
import {Handle, NodeProps, Position} from 'reactflow';


export default function TelegramSenderNode(props: NodeProps) {
    return (
        <>
            <Handle
                type="target"
                position={Position.Top}
                onConnect={(params) => console.log('handle onConnect', params)}
                isConnectable={true}
            />
            <div className={"flex flex-col border-2 rounded-lg"}>
                <div>Message:</div>
                <input id="text" name="text" className="nodrag border"/>
                <div>Number:</div>
                <input id="text" name="text" className="nodrag border"/>

            </div>
            <Handle type="source" position={Position.Bottom} id="a"/>
            <Handle type="source" position={Position.Bottom} id="b"/>
        </>
    );

}
import {useCallback} from 'react';
import {Handle, NodeProps, Position} from 'reactflow';

function TextInputNode(props: NodeProps) {
    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);

    return (
        <>
            <div className={"flex flex-col border-2 rounded-lg"}>
                <div>Text Input:</div>
                <input id="text" name="text" onChange={onChange} className="nodrag border"/>
                <div className={"flex flex-row items-center justify-between p-2"}>
                    <div>Check me:</div>
                    <input type="checkbox"/>
                </div>

            </div>
            <Handle type="source" position={Position.Bottom} id="a"/>
            <Handle type="source" position={Position.Bottom} id="b"/>
        </>
    );
}

export default TextInputNode
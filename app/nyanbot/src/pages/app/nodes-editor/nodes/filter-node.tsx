import {Handle, NodeProps, Position} from "reactflow";
import {FC, useCallback, useState} from "react";
import handle from "@/pages/app/nodes-editor/nodes/handle.module.css";
import node from "@/pages/app/nodes-editor/nodes/node.module.css";
import {Button} from "@/components/ui/button.tsx";

type Condition = {
    id: number;
}

export default function FilterNode(props: NodeProps) {
    const [conditions, setConditions] = useState<Condition[]>([
        {id: 1}
    ]);

    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);

    function addCondition() {
        setConditions(prevState => [...prevState, {id: prevState.length + 1}]);
    }


    const FilterCondition: FC<Condition> = ({id}) => {
        return (
            <div className={" m-2 border-2 border-amber-300 w-full"}>
                <div className={"p-2"}>If
                    <input placeholder={"condition"}/>
                </div>
                <div className={"p-2"}>
                    then
                    <Handle type="source" position={Position.Right} id="true" className={`
                ${handle.boolean}
                ${handle.right}
                `}/>
                </div>

            </div>
        )
    }

    return (
        <>
            <div className={node.filterNode}>
                <div className={node.header}>Filter Node</div>
                {conditions.map((c) =>
                    <FilterCondition key={c.id} id={c.id}/>
                )}
                <Button onClick={addCondition}>+</Button>
            </div>
            <Handle type="target" position={Position.Left} id="true" className={handle.boolean}/>

        </>
    );
}


/*
<Handle type="source" position={Position.Right} id="boolean"
        className={`${handle.boolean} ${handle.right}`}/>*/

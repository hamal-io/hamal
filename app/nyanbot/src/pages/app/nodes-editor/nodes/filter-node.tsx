import {Handle, NodeProps, Position, useUpdateNodeInternals} from "reactflow";
import {useCallback, useEffect, useState} from "react";
import handle from "@/pages/app/nodes-editor/nodes/handle.module.css";
import node from "@/pages/app/nodes-editor/nodes/node.module.css";
import {Button} from "@/components/ui/button.tsx";

type Condition = {
    id: number
}

export default function FilterNode(props: NodeProps) {
    const [conditions, setConditions] = useState<Condition[]>([{id: 0}])
    const updateNodeInternals = useUpdateNodeInternals();

    const onChange = useCallback((evt) => {
        console.log(evt.target.value);
    }, []);

    const addCondition = () => {
        setConditions(prevState => [...prevState, {id: conditions.length + 1}])

    }

    useEffect(() => {
        updateNodeInternals(props.id);
    }, [conditions, props.id, updateNodeInternals]);

    const FilterCondition = () => {
        return (
            <div className={" m-2 border-b-2 w-full"}>
                <div>
                    <div className={"p-2"}>
                        <select>
                            <option value="total_value"> {"total_value"} </option>
                            <option value="created_at">{"created_at"}</option>
                            <option value="holders">{"holders"}</option>
                            <option value="buyers_today">{"buyers_today"}</option>
                        </select>
                    </div>
                    <div className={"p-2"}>
                        <select>
                            <option value=">"> {">"} </option>
                            <option value="<">{"<"}</option>
                            <option value="==">{"=="}</option>
                            <option value="!=">{"!="}</option>
                        </select>
                    </div>
                    <div className={"p-2"}>
                        <input placeholder={"condition"}/>
                    </div>
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
                {
                    conditions.map((c) =>
                        <FilterCondition key={c.id}/>
                    )
                }
                <Button onClick={addCondition} className={"border-2"}>+</Button>
            </div>
            <Handle type="target" position={Position.Top} id="true" className={handle.boolean}/>
        </>
    );
}


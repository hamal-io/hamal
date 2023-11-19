import React, {FC, useEffect} from "react";
import {columns} from "@/pages/app/flow-detail/pages/exec-list/components/list/components/columns.tsx";
import Table from "@/pages/app/flow-detail/pages/exec-list/components/list/components/table.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useExecList} from "@/hook/exec.ts";

type FlowProps = {
    id: string;
    name: string;
}

type ListProps = {
    flow: FlowProps
}

const List: FC<ListProps> = ({flow}) => {
    const [listExecs, execList, isLoading, error] = useExecList()

    useEffect(() => {
        const abortController = new AbortController()
        listExecs(flow.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [flow.id]);

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Executions"
                description={`Executions of your flow ${flow.name}`}
                actions={[]}
            />
            <Table data={execList.execs} columns={columns}/>
        </div>
    );
}

export default List;
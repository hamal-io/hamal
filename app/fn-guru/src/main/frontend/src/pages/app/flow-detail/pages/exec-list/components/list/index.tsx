import React, {FC} from "react";
import {useApiExecList} from "@/hook/api/exec.ts";
import {ApiFlowSimple} from "@/api/types";
import {columns} from "@/pages/app/flow-detail/pages/exec-list/components/list/components/columns.tsx";
import Table from "@/pages/app/flow-detail/pages/exec-list/components/list/components/table.tsx";
import {PageHeader} from "@/components/page-header.tsx";

type ListProps = {
    flow: ApiFlowSimple
}

const List: FC<ListProps> = ({flow}) => {
    const [execs, isLoading, error] = useApiExecList(flow.id)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Executions"
                description={`Executions of your flow ${flow.name}`}
                actions={[]}
            />
            <Table data={execs} columns={columns}/>
        </div>
    );
}

export default List;
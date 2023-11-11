import React, {FC} from "react";
import {useApiExecList} from "@/hook/api/exec.ts";
import {ApiFlowSimple, ApiExecSimple} from "@/api/types";
import {columns} from "@/pages/app/flow-detail/pages/exec-list/components/components/columns.tsx";
import Table from "@/pages/app/flow-detail/pages/exec-list/components/components/table.tsx";
import {PageHeader} from "@/components/page-hader.tsx";
import Create from "@/pages/app/flow-detail/pages/func-list/components/create.tsx";
import {Separator} from "@/components/ui/separator.tsx";

type ListProps = {
    flow: ApiFlowSimple
}

const List: FC<ListProps> = ({flow}) => {
    const [execs, isLoading, error] = useApiExecList(flow.id)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Executions"
                description={`Executions of your flow ${flow.name}`}
                actions={[]}
            />
            <Separator className="my-6"/>
            <Table data={execs} columns={columns}/>
        </div>
    );
}

export default List;
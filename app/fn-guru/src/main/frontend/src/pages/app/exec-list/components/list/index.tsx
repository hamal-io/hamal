import React, {FC, useEffect} from "react";
import {columns} from "@/pages/app/exec-list/components/list/components/columns.tsx";
import Table from "@/pages/app/exec-list/components/list/components/table.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useExecList} from "@/hook/exec.ts";

type GroupProps = {
    id: string;
    name: string;
}

type ListProps = {
    group: GroupProps
}

const List: FC<ListProps> = ({group}) => {
    const [listExecs, execList, isLoading, error] = useExecList()

    useEffect(() => {
        const abortController = new AbortController()
        listExecs(group.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [group.id]);

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Executions"
                description={`Executions of your group ${group.name}`}
                actions={[]}
            />
            <Table data={execList.execs} columns={columns}/>
        </div>
    );
}

export default List;
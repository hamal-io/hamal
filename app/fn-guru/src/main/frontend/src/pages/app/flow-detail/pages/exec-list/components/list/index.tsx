import React, {FC, useEffect} from "react";
import {columns} from "@/pages/app/namespace-detail/pages/exec-list/components/list/components/columns.tsx";
import Table from "@/pages/app/namespace-detail/pages/exec-list/components/list/components/table.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useExecList} from "@/hook/exec.ts";

type NamespaceProps = {
    id: string;
    name: string;
}

type ListProps = {
    namespace: NamespaceProps
}

const List: FC<ListProps> = ({namespace}) => {
    const [listExecs, execList, isLoading, error] = useExecList()

    useEffect(() => {
        const abortController = new AbortController()
        listExecs(namespace.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [namespace.id]);

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Executions"
                description={`Executions of your namespace ${namespace.name}`}
                actions={[]}
            />
            <Table data={execList.execs} columns={columns}/>
        </div>
    );
}

export default List;
import React, {FC, useContext, useEffect} from "react";
import {GroupLayoutContext} from "@/components/app/layout";
import {useExecList} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import {columns} from "@/pages/app/exec-list/components/columns.tsx";
import Table from "@/pages/app/exec-list/components/table.tsx";

type Props = {}

const ExecListPage: FC<Props> = () => {
    const {groupId, groupName, namespaceId} = useContext(GroupLayoutContext)
    const [listExecs, execList, isLoading, error] = useExecList()

    useEffect(() => {
        const abortController = new AbortController()
        listExecs(namespaceId, abortController)
        return () => {
            abortController.abort()
        }
    }, [groupId]);

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Executions"
                description={`Executions of your namespace TBD`}
                actions={[]}
            />
            <Table data={execList.execs} columns={columns}/>
        </div>
    );
}

export default ExecListPage
import React, {FC, useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {columns} from "@/pages/app/exec-list/components/columns.tsx";
import Table from "@/pages/app/exec-list/components/table.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useExecsWithTriggers} from "@/pages/app/exec-list/components/hook.ts";


type Props = {}
const ExecListPage: FC<Props> = () => {
    const [uiState] = useUiState()
    const [listExecs, execList, loading, error] = useExecsWithTriggers()

    useEffect(() => {
        const abortController = new AbortController()
        listExecs(uiState.namespaceId, abortController)
        return () => {
            abortController.abort()
        }
    }, [uiState.namespaceId]);

    if (execList == null || loading) return "Loading..."
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
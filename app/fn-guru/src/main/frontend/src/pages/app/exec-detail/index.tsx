import React, {FC, useEffect} from "react";
import {useParams} from "react-router-dom";
import {useExecGet} from "@/hook";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Log from "@/pages/app/exec-detail/components/log.tsx";

type Props = {}
const ExecDetailPage: FC<Props> = () => {
    const {execId} = useParams()
    const [getExec, exec, loading] = useExecGet()

    useEffect(() => {
        getExec(execId)
    }, [execId]);

    if (exec == null || loading) {
        return "Loading..."
    }


    return (
        <div>
            <PageHeader
                title={`Execution ${execId}`}
                actions={[]}
            />
            <Tabs defaultValue="overview" className="space-y-4">
                <TabsList>
                    <TabsTrigger value="overview">Overview</TabsTrigger>
                    <TabsTrigger value="logs">Logs</TabsTrigger>
                    <TabsTrigger value="reports" disabled>Reports</TabsTrigger>
                    <TabsTrigger value="notifications" disabled>Notifications</TabsTrigger>
                </TabsList>

                <TabsContent value="overview" className="space-y-4">
                    <h1>Overview</h1>
                </TabsContent>

                <TabsContent value="logs" className="space-y-4">
                    <Log execId={execId}/>
                </TabsContent>
            </Tabs>
        </div>
    )
}

export default ExecDetailPage
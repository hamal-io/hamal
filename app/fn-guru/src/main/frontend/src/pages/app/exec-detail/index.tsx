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
                title={` ${exec.status} Execution ${execId}`}
                actions={[]}
            />
            <Tabs defaultValue="overview" className="space-y-4">
                <TabsList>
                    <TabsTrigger value="overview">Overview</TabsTrigger>
                    <TabsTrigger value="logs">Logs</TabsTrigger>
                    <TabsTrigger value="result">Result</TabsTrigger>
                    <TabsTrigger value="state">State</TabsTrigger>
                </TabsList>

                <TabsContent value="overview" className="space-y-4">
                    <h1>Overview</h1>
                    <div className="flex flex-col">
                        <ul>
                            <li>Id: {exec.id} </li>
                            <li>Status: {exec.status} </li>
                            {exec.func && <li>Func: {exec?.func?.name}</li>}
                            {exec.correlation && <li>Correlation: {exec.correlation}</li>}
                        </ul>


                    </div>

                </TabsContent>

                <TabsContent value="logs" className="space-y-4">
                    <Log execId={execId}/>
                </TabsContent>

                <TabsContent value="result" className="space-y-4">
                    <section>
                        {JSON.stringify(exec.result)}
                    </section>
                </TabsContent>

                <TabsContent value="state" className="space-y-4">
                    <section>
                        {JSON.stringify(exec.state)}
                    </section>
                </TabsContent>
            </Tabs>
        </div>
    )
}

export default ExecDetailPage
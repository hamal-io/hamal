import React, {FC, useContext} from "react";
import {FlowContext} from "@/pages/app/flow-detail";
import Table from "@/pages/app/flow-detail/pages/exec-list/components/components/table.tsx";
import {columns} from "@/pages/app/flow-detail/pages/exec-list/components/components/columns.tsx";

type Props = {}

import {z} from "zod";
import {taskSchema} from "@/pages/app/flow-detail/pages/exec-list/components/data/schema.ts";
import {useApiExecList} from "@/hook/api/exec.ts";

function getTasks() {
    const data = [
        {
            "id": "TASK-8782",
            "title": "You can't compress the program without quantifying the open-source SSD pixel!",
            "status": "Planned",
            "label": "documentation",
            "priority": "medium"
        },
        {
            "id": "TASK-3802",
            "title": "Try to override the ASCII protocol, maybe it will parse the virtual matrix!",
            "status": "Scheduled",
            "label": "feature",
            "priority": "low"
        },
        {
            "id": "TASK-2007",
            "title": "I'll input the back-end USB protocol, that should bandwidth the PCI system!",
            "status": "Queued",
            "label": "bug",
            "priority": "high"
        },
        {
            "id": "TASK-7516",
            "title": "Use the primary SQL program, then you can generate the auxiliary transmitter!",
            "status": "Started",
            "label": "documentation",
            "priority": "medium"
        },
        {
            "id": "TASK-6906",
            "title": "Try to back up the DRAM system, maybe it will reboot the online transmitter!",
            "status": "Completed",
            "label": "feature",
            "priority": "high"
        },
        {
            "id": "TASK-5207",
            "title": "The SMS interface is down, copy the bluetooth bus so we can quantify the VGA card!",
            "status": "Failed",
            "label": "bug",
            "priority": "low"
        }
    ]


    return z.array(taskSchema).parse(data)
}

const ExecListPage: FC<Props> = () => {
    const flow = useContext(FlowContext)

    const [execs, isLoading, error] = useApiExecList(flow.id)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    const tasks = getTasks()
    return (
        // <List flow={flow}/>
        <div className="hidden h-full flex-1 flex-col space-y-8 p-8 md:flex">
            <div className="flex items-center justify-between space-y-2">
                <div>
                    <h2 className="text-2xl font-bold tracking-tight">Welcome back!</h2>
                    <p className="text-muted-foreground">
                        Here&apos;s a list of your tasks for this month!
                    </p>
                </div>
                <div className="flex items-center space-x-2">
                </div>
            </div>
            <Table data={tasks} columns={columns}/>
        </div>
    )
}

export default ExecListPage
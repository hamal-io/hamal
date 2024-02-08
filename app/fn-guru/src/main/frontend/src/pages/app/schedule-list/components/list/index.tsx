import React, {FC, useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import Table from "@/pages/app/schedule-list/components/list/components/table.tsx";
import {columns} from "@/pages/app/schedule-list/components/list/components/columns.tsx";
import CreateFixedRate from "@/pages/app/schedule-list/components/create/fixed-rate.tsx";
import {useTriggerListSchedule} from "@/hook";

type flowProps = {
    id: string;
    name: string;
}

type ListProps = {
    flow: flowProps
}

const List: FC<ListProps> = ({flow}) => {
    const [listSchedules, scheduleList, loading, error] = useTriggerListSchedule()

    useEffect(() => {
        const abortController = new AbortController()
        listSchedules(flow.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [flow.id]);

    if (loading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Schedules"
                description={`Periodically call your functions of flow ${flow.name}`}
                actions={[
                    // <CreateEvery flow={flow}/>,
                    <CreateFixedRate flow={flow}/>
                ]}
            />
            <Table data={scheduleList.triggers} columns={columns}/>
        </div>
    );
}

export default List;
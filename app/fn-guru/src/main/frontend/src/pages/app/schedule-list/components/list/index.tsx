import React, {FC, useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import Table from "@/pages/app/schedule-list/components/list/components/table.tsx";
import {columns} from "@/pages/app/schedule-list/components/list/components/columns.tsx";
import CreateSchedule from "@/pages/app/schedule-list/components/create";
import {useTriggerListSchedule} from "@/hook";

type GroupProps = {
    id: string;
    name: string;
}

type ListProps = {
    group: GroupProps
}

const List: FC<ListProps> = ({group}) => {
    const [listSchedules, scheduleList, loading, error] = useTriggerListSchedule()

    useEffect(() => {
        const abortController = new AbortController()
        listSchedules(group.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [group.id]);

    if (loading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Schedules"
                description={`Periodically call your function`}
                actions={[
                    <CreateSchedule group={group}/>
                ]}
            />
            <Table data={scheduleList.triggers} columns={columns}/>
        </div>
    );
}

export default List;
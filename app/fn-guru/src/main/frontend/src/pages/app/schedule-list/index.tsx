import React, {FC, useContext, useEffect} from "react";
import {GroupLayoutContext} from "@/components/app/layout";
import {useTriggerListSchedule} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import {columns} from "@/pages/app/schedule-list/components/columns.tsx";
import Table from "@/pages/app/schedule-list/components/table.tsx";
import CreateFixedRate from "@/pages/app/schedule-list/components/create.tsx";

type Props = {}
const ScheduleListPage: FC<Props> = () => {
    const {groupId, groupName} = useContext(GroupLayoutContext)
    const [listSchedules, scheduleList, loading, error] = useTriggerListSchedule()

    useEffect(() => {
        const abortController = new AbortController()
        listSchedules(groupId, abortController)
        return () => {
            abortController.abort()
        }
    }, [groupId]);

    if (loading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Schedules"
                description={`Schedules TBD`}
                actions={[
                    <CreateFixedRate/>
                ]}
            />
            <Table data={scheduleList.triggers} columns={columns}/>
        </div>
    );
}

export default ScheduleListPage
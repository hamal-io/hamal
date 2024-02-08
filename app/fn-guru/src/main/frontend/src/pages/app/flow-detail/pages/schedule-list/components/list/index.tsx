import React, {FC, useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import Table from "@/pages/app/namespace-detail/pages/schedule-list/components/list/components/table.tsx";
import {columns} from "@/pages/app/namespace-detail/pages/schedule-list/components/list/components/columns.tsx";
import CreateFixedRate from "@/pages/app/namespace-detail/pages/schedule-list/components/create/fixed-rate.tsx";
import {useTriggerListSchedule} from "@/hook";

type NamespaceProps = {
    id: string;
    name: string;
}

type ListProps = {
    namespace: NamespaceProps
}

const List: FC<ListProps> = ({namespace}) => {
    const [listSchedules, scheduleList, loading, error] = useTriggerListSchedule()

    useEffect(() => {
        const abortController = new AbortController()
        listSchedules(namespace.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [namespace.id]);

    if (loading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Schedules"
                description={`Periodically call your functions of namespace ${namespace.name}`}
                actions={[
                    // <CreateEvery namespace={namespace}/>,
                    <CreateFixedRate namespace={namespace}/>
                ]}
            />
            <Table data={scheduleList.triggers} columns={columns}/>
        </div>
    );
}

export default List;
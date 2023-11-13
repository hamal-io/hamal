import React, {FC} from "react";
import {ApiFlowSimple} from "@/api/types";
import {PageHeader} from "@/components/page-header.tsx";
import {useListScheduleTriggers} from "@/hook/api/schedule.tsx";
import Table from "@/pages/app/flow-detail/pages/schedule-list/components/list/components/table.tsx";
import {columns} from "@/pages/app/flow-detail/pages/schedule-list/components/list/components/columns.tsx";
import Create from "@/pages/app/flow-detail/pages/schedule-list/components/create";

type ListProps = {
    flow: ApiFlowSimple
}

const List: FC<ListProps> = ({flow}) => {
    const [schedules, isLoading, error] = useListScheduleTriggers(flow.id)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2 mb-6">
            <PageHeader
                title="Schedules"
                description={`Periodically call your functions of flow ${flow.name}`}
                actions={[
                    <Create flow={flow}/>
                ]}
            />
            <Table data={schedules} columns={columns}/>
        </div>
    );
}

export default List;
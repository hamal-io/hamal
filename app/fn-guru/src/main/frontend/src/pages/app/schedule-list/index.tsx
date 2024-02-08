import React, {FC, useContext} from "react";
import {flowContext} from "@/pages/app/flow-detail";
import List from "@/pages/app/schedule-list/components/list";

type Props = {}

const ScheduleListPage: FC<Props> = () => {
    const flow = useContext(flowContext)

    if (flow == null) {
        return "Loading..."
    }

    return (
        <List flow={flow}/>
    );
}

export default ScheduleListPage
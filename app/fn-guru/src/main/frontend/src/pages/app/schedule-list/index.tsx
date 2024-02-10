import React, {FC, useContext} from "react";
import List from "@/pages/app/schedule-list/components/list";
import {GroupContext} from "@/components/app/layout";

type Props = {}
const ScheduleListPage: FC<Props> = () => {
    const group = useContext(GroupContext)

    if (group == null) {
        return "Loading..."
    }

    return (
        <List group={group}/>
    );
}

export default ScheduleListPage
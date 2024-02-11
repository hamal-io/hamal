import React, {FC, useContext} from "react";
import List from "@/pages/app/schedule-list/components/list";
import {GroupLayoutContext} from "@/components/app/layout";

type Props = {}
const ScheduleListPage: FC<Props> = () => {
    const {groupId, groupName} = useContext(GroupLayoutContext)
    return (<List group={{id: groupId, name: groupName}}/>)
}

export default ScheduleListPage
import React, {FC, useContext} from "react";
import {flowContext} from "@/pages/app/flow-detail";
import List from "@/pages/app/exec-list/components/list";
import {GroupLayoutContext} from "@/components/app/layout";

type Props = {}

const ExecListPage: FC<Props> = () => {
    const {groupId, groupName} = useContext(GroupLayoutContext)
    return (<List group={{id: groupId, name: groupName}}/>)
}

export default ExecListPage
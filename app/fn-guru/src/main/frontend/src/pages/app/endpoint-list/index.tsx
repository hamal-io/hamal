import React, {FC, useContext} from "react";
import List from "@/pages/app/endpoint-list/components/list.tsx";
import {GroupLayoutContext} from "@/components/app/layout";

type Props = {}
const EndpointListPage: FC<Props> = ({}) => {
    const {groupId, groupName} = useContext(GroupLayoutContext)
    return (<List group={{id: groupId, name: groupName}}/>)
}

export default EndpointListPage
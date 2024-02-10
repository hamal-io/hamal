import React, {FC, useContext} from "react";
import List from "@/pages/app/endpoint-list/components/list.tsx";
import {GroupContext} from "@/components/app/layout";

type Props = {}
const EndpointListPage: FC<Props> = ({}) => {
    const group = useContext(GroupContext)
    if (group == null) {
        return "Loading..."
    }
    return (<List group={group}/>)
}

export default EndpointListPage
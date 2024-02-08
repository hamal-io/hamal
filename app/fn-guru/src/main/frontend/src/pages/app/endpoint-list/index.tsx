import React, {FC, useContext} from "react";
import {flowContext} from "@/pages/app/flow-detail";
import List from "@/pages/app/endpoint-list/components/list.tsx";

type Props = {}
const EndpointListPage: FC<Props> = ({}) => {
    const flow = useContext(flowContext)
    if (flow == null) {
        return "Loading..."
    }
    return (<List flow={flow}/>)
}

export default EndpointListPage
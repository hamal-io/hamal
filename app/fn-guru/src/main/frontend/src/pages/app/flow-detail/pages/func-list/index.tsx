import React, {FC, useContext} from "react";
import {FlowContext} from "@/pages/app/flow-detail";
import List from "@/pages/app/flow-detail/pages/func-list/components/list.tsx";

type Props = {}
const FuncListPage: FC<Props> = ({}) => {
    const flow = useContext(FlowContext)
    if (flow == null) {
        return "Loading..."
    }
    return (<List flow={flow}/>)
}

export default FuncListPage
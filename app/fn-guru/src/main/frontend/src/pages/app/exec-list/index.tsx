import React, {FC, useContext} from "react";
import {flowContext} from "@/pages/app/flow-detail";
import List from "@/pages/app/exec-list/components/list";

type Props = {}

const ExecListPage: FC<Props> = () => {
    const flow = useContext(flowContext)

    if (flow == null) {
        return "Loading..."
    }

    return (
        <List flow={flow}/>
    );
}

export default ExecListPage
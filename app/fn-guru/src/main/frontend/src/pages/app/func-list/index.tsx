import React, {FC, useContext} from "react";
import {flowContext} from "@/pages/app/flow-detail";
import List from "@/pages/app/func-list/components/list.tsx";
import {GroupContext} from "@/components/app/layout";

type Props = {}
const FuncListPage: FC<Props> = ({}) => {
    const group = useContext(GroupContext)
    if (group == null) {
        return "Loading..."
    }
    return (<List group={group}/>)
}

export default FuncListPage
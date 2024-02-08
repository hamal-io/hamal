import React, {FC, useContext} from "react";
import {NamespaceContext} from "@/pages/app/namespace-detail";
import List from "@/pages/app/namespace-detail/pages/exec-list/components/list";

type Props = {}

const ExecListPage: FC<Props> = () => {
    const namespace = useContext(NamespaceContext)

    if (namespace == null) {
        return "Loading..."
    }

    return (
        <List namespace={namespace}/>
    );
}

export default ExecListPage
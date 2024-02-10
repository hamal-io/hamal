import React from 'react'
import {useAuth} from "@/hook/auth.ts";
import List from "@/pages/app/flow-list/components/list.tsx";

const flowListPage: React.FC = () => {
    const [auth] = useAuth()
    return (
        <div>
            <List groupId={auth.groupId}/>
        </div>

    )
}


export default flowListPage;

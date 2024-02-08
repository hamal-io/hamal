import React from 'react'
import {useAuth} from "@/hook/auth.ts";
import List from "@/pages/app/namespace-list/components/list.tsx";

const NamespaceListPage: React.FC = () => {
    const [auth] = useAuth()
    return (
        <div>
            <List groupId={auth.groupId}/>
        </div>

    )
}


export default NamespaceListPage;

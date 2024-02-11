import React, {createContext, FC, ReactNode, useEffect} from 'react'
import Index from "@/components/app/layout/authenticated";
import {useParams} from "react-router-dom";
import {useGroupGet, useNamespaceGet} from "@/hook";
import GroupHeader from "./header.tsx";

type Props = {
    children: ReactNode;
}

export const GroupLayoutContext = createContext<
    {
        groupId: string;
        groupName: string;
        namespaceId: string;
        namespaceName: string;
    } | null
>(null)


const GroupLayout: FC<Props> = ({children}) => {
    const {groupId, namespaceId} = useParams()
    const [getGroup, group] = useGroupGet()
    const [getNamespace, namespace] = useNamespaceGet()

    useEffect(() => {
        getGroup(groupId)
    }, [groupId]);

    useEffect(() => {
        getNamespace((namespaceId))
    }, [namespaceId]);


    if (group == null || namespace == null) {
        return ("Loading..")
    }

    return (
        <Index>
            <GroupLayoutContext.Provider value={{
                groupId: group.id,
                groupName: group.name,
                namespaceId: namespace.id,
                namespaceName: namespace.name
            }}>
                <main className="flex-col md:flex">
                    <GroupHeader/>
                    {children}
                </main>
            </GroupLayoutContext.Provider>
        </Index>
    );
}

export default GroupLayout;


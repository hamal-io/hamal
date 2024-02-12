import React, {createContext, FC, ReactNode, useEffect} from 'react'
import Authenticated from "@/components/app/layout/authenticated";
import {useParams} from "react-router-dom";
import {useGroupGet, useNamespaceGet} from "@/hook";
import Sidebar from "@/components/app/layout/group/sidebar.tsx";

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
        <Authenticated>
            <GroupLayoutContext.Provider value={{
                groupId: group.id,
                groupName: group.name,
                namespaceId: namespace.id,
                namespaceName: namespace.name
            }}>

                <div className="flex flex-row min-h-screen ">
                    <Sidebar/>
                    <div className="p-4 border-l border-border w-full ml-48">
                        {children}
                    </div>
                </div>

            </GroupLayoutContext.Provider>
        </Authenticated>
    );
}

export default GroupLayout;


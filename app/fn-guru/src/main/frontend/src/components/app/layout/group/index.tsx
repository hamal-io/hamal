import React, {createContext, FC, ReactNode, useEffect} from 'react'
import Index from "@/components/app/layout/authenticated";
import {useParams} from "react-router-dom";
import {useGroupGet} from "@/hook";
import {Group} from "@/types";
import GroupHeader from "./header.tsx";

type Props = {
    children: ReactNode;
}

export const GroupLayoutContext = createContext<
    {
        groupId: string;
        groupName: string;
        namespaceId: string;
    } | null
>(null)


const GroupLayout: FC<Props> = ({children}) => {
    const {groupId, namespaceId} = useParams()

    const [getGroup, group, loading, error] = useGroupGet()
    useEffect(() => {
        getGroup(groupId)
    }, [groupId]);

    if (loading) {
        return ("Loading..")
    }

    return (
        <Index>
            <GroupLayoutContext.Provider value={{
                groupId: group.id,
                groupName: group.name,
                namespaceId: namespaceId
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


import React, {createContext, FC, ReactNode, useEffect} from 'react'
import Index from "@/components/app/layout/authenticated";
import {useParams} from "react-router-dom";
import {useGroupGet} from "@/hook";
import {Group} from "@/types";
import GroupHeader from "./header.tsx";

type Props = {
    children: ReactNode;
}

export const GroupContext = createContext<Group | null>(null)


const GroupLayout: FC<Props> = ({children}) => {
    const {groupId} = useParams()

    const [getGroup, group, loading, error] = useGroupGet()
    useEffect(() => {
        getGroup(groupId)
    }, [groupId]);


    return (
        <Index>
            <GroupContext.Provider value={group}>
                <main className="flex-col md:flex">
                    <GroupHeader/>
                    {children}
                </main>
            </GroupContext.Provider>
        </Index>
    );
}

export default GroupLayout;


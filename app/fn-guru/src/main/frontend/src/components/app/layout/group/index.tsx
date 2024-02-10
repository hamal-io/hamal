import React, {createContext, FC, ReactNode, useEffect} from 'react'
import Authenticated from "@/components/app/authenticated.tsx";
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
        <Authenticated>
            <GroupContext.Provider value={group}>
                <main className="flex-col md:flex">
                    <GroupHeader/>
                    {children}
                </main>
            </GroupContext.Provider>
        </Authenticated>
    );
}

export default GroupLayout;


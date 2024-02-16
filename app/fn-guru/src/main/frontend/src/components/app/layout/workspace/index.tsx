import React, {createContext, FC, ReactNode, useEffect} from 'react'
import Authenticated from "@/components/app/layout/authenticated";
import Sidebar from "@/components/app/layout/workspace/sidebar.tsx";

type Props = {
    children: ReactNode;
}

const WorkspaceLayout: FC<Props> = ({children}) => {
    return (
        <Authenticated>
            <div className="flex flex-row min-h-screen ">
                <Sidebar/>
                <div className="border-l border-border w-full ml-48">
                    {children}
                </div>
            </div>
        </Authenticated>
    );
}

export default WorkspaceLayout;


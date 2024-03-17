import React, {FC, ReactNode} from "react";
import Authenticated from "@/components/app/layout/authenticated";
import WorkspaceSidebar from "@/pages/app/workspace-detail/components/sidebar.tsx";


type Props = { children: ReactNode; }
const WorkspaceDetailPage: FC<Props> = ({children}) => {
    return (
        <Authenticated>
            <div className="flex flex-row min-h-screen ">
                <WorkspaceSidebar/>
                <div className="border-border w-full ml-48">
                    {children}
                </div>
            </div>
        </Authenticated>
    );
}

export default WorkspaceDetailPage;

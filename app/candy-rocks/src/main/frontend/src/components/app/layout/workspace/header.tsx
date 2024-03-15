import React, {FC} from "react";
import NamespaceSelector from "@/components/app/layout/workspace/namespace-selector.tsx";


const Header: FC = () => {
    return (
        <div className=" pl-48 border-b bg-white">
            <div className="flex flex-copl h-16 pt-4 px-4">
                <div >
                    <NamespaceSelector/>
                </div>
            </div>
        </div>
    )
}

export default Header
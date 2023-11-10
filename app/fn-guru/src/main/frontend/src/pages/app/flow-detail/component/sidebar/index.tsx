import React, {FC} from 'react'
// import {ListGroup} from 'flowbite-react';

import {useNavigate, useParams} from "react-router-dom";

const itemTheme = {
    base: "",
    link: {
        base: "flex items-center w-full border-b border-gray-200 py-2 px-4 dark:border-gray-600",
        active: {
            off: "bg-gray-200 hover:bg-gray-900 hover:text-gray-100 focus:text-gray-700",
            on: "bg-gray-200 text-white"
        },
        href: {
            off: "",
            on: ""
        },
        icon: "mr-2 h-4 w-4 fill-current"
    }
}

const Flowsidebar: FC = () => {
    const {flowId} = useParams()
    const navigate = useNavigate()
    return (
        <div className="flex justify-center">
            {/*<ListGroup*/}
            {/*    className="w-48"*/}
            {/*>*/}
            {/*    <ListGroup.Item theme={itemTheme} onClick={() => navigate(`/flows/${flowId}/executions`)}>Execution</ListGroup.Item>*/}
            {/*    <ListGroup.Item theme={itemTheme} onClick={() => navigate(`/flows/${flowId}/functions`)}>Function</ListGroup.Item>*/}
            {/*</ListGroup>*/}
        </div>
    );
}

export default Flowsidebar


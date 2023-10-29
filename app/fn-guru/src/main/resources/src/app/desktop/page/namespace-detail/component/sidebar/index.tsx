import React, {FC} from 'react'
import {Sidebar} from 'flowbite-react';

import {HiChartPie} from 'react-icons/hi';
import {useNavigate, useParams} from "react-router-dom";

const NamespaceSidebar: FC = () => {
    const {namespaceId} = useParams()
    const navigate = useNavigate()
    return (
        <Sidebar aria-label="Namespace sidebar">
            <Sidebar.Items>
                <Sidebar.ItemGroup>
                    <Sidebar.Item
                        onClick={() => navigate(`/namespaces/${namespaceId}`)}
                    >
                        <p>
                            Dashboard
                        </p>
                    </Sidebar.Item>
                    <Sidebar.Item
                        onClick={() => navigate(`/namespaces/${namespaceId}/executions`)}
                    >
                        <p>
                            Execution
                        </p>
                    </Sidebar.Item>
                    <Sidebar.Item
                        href="#"
                    >
                        <p>
                            Log
                        </p>
                    </Sidebar.Item>
                    <Sidebar.Item
                        onClick={() => navigate(`/namespaces/${namespaceId}/functions`)}
                    >
                        <p>
                            Function
                        </p>
                    </Sidebar.Item>
                    <Sidebar.Item
                        href="#"
                    >
                        <p>
                            Trigger
                        </p>
                    </Sidebar.Item>
                    <Sidebar.Item
                        href="#"
                    >
                        <p>
                            Webhook
                        </p>
                    </Sidebar.Item>
                </Sidebar.ItemGroup>
            </Sidebar.Items>
        </Sidebar>
    );
}

export default NamespaceSidebar


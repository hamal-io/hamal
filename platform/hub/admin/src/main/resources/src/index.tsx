import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Flowbite} from 'flowbite-react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

import DashboardPage from "./page/dashboard";
import AdhocPage from "./page/adhoc";
import Navbar from "./component/navbar";

import Footer from "./component/footer";
import FuncListPage from "./page/func-list";
import LogListPage from "./page/log-list";
import TriggerListPage from "./page/trigger-list";
import ExecListPage from "./page/exec-list";
import AccountListPage from "./page/account-list";
import GroupListPage from "./page/group-list";

const DefaultPage: React.FC<DefaultPageProps> = (props) => {
    return (
        <div className="flex flex-col h-screen justify-between">
            <Navbar/>
            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                {props.children}
            </main>
            <Footer/>
        </div>
    );
}

const router = createBrowserRouter([
    {
        path: "/accounts",
        element: <DefaultPage><AccountListPage/></DefaultPage>
    },
    {
        path: "/",
        element: <DefaultPage><DashboardPage/></DefaultPage>
    },
    {
        path: "/adhoc",
        element: <DefaultPage><AdhocPage/></DefaultPage>
    },
    {
        path: "/executions",
        element: <DefaultPage><ExecListPage/></DefaultPage>
    },
    {
        path: "/functions",
        element: <DefaultPage><FuncListPage/></DefaultPage>
    },
    {
        path: "/groups",
        element: <DefaultPage><GroupListPage/></DefaultPage>
    },
    {
        path: "/logs",
        element: <DefaultPage><LogListPage/></DefaultPage>
    },
    {
        path: "/triggers",
        element: <DefaultPage><TriggerListPage/></DefaultPage>
    },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Flowbite theme={{theme: Theme}}>
            <RouterProvider router={router}/>
        </Flowbite>
    </React.StrictMode>,
)

interface DefaultPageProps {
    children: string | JSX.Element | JSX.Element[]
}


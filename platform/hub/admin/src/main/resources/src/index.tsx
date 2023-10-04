import React, {useState} from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Button, Flowbite} from 'flowbite-react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

import DashboardPage from "./page/dashboard";
import SignInPage from "./page/sign-in";
import AdhocPage from "./page/adhoc";
import Navbar from "./component/navbar";

import Footer from "./component/footer";
import FuncListPage from "./page/func-list";

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
        path: "/",
        element: <DefaultPage><DashboardPage/></DefaultPage>
    },
    {
        path: "/adhoc",
        element: <DefaultPage><AdhocPage/></DefaultPage>
    },
    {
        path: "/functions",
        element: <DefaultPage><FuncListPage/></DefaultPage>
    },
    {
        path: "/sign-in",
        element: <SignInPage/>
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


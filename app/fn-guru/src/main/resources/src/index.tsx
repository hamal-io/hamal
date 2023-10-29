import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Flowbite} from 'flowbite-react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

// public
import HomePage from "./landing/page/home";
import SignInPage from "./landing/page/sign-in";
import SignUpPage from "./landing/page/sign-up";

// app
import AdhocPage from "./app/desktop/page/adhoc";
import AuthenticatedPage from "./app/desktop/page/authenticated";
import DashboardPage from "./app/desktop/page/dashboard";

import ExecDetailPage from "./app/desktop/page/exec-detail";
import ExecListPage from "./app/desktop/page/exec-list";

import FuncDetail from "./app/desktop/page/func-detail";
import FuncListPage from "./app/desktop/page/func-list";

import NamespaceListPage from "./app/desktop/page/namespace-list";
import NamespaceDetailPage from "./app/desktop/page/namespace-detail";


const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/sign-in", element: <SignInPage/>},
    {path: "/sign-up", element: <SignUpPage/>},

    {path: "/dashboard", element: <AuthenticatedPage><DashboardPage/></AuthenticatedPage>},
    {path: "/execs", element: <AuthenticatedPage><ExecListPage/></AuthenticatedPage>},
    {path: "/execs/:execId", element: <AuthenticatedPage><ExecDetailPage/></AuthenticatedPage>},

    {path: "/functions", element: <AuthenticatedPage><FuncListPage/></AuthenticatedPage>},
    {path: "/functions/:funcId", element: <AuthenticatedPage><FuncDetail/></AuthenticatedPage>},

    {path: "/namespaces", element: <AuthenticatedPage><NamespaceListPage/></AuthenticatedPage>},
    {path: "/namespaces/:namespaceId", element: <AuthenticatedPage><NamespaceDetailPage/></AuthenticatedPage>},

    {path: "/play", element: <AuthenticatedPage><AdhocPage/></AuthenticatedPage>}
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Flowbite theme={{theme: Theme}}>
            <RouterProvider router={router}/>
        </Flowbite>
    </React.StrictMode>,
)


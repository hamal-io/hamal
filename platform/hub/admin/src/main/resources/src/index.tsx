import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Flowbite} from 'flowbite-react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

import DashboardPage from "./page/dashboard";
import SignInPage from "./page/sign-in";
import AdhocPage from "./page/adhoc";

const router = createBrowserRouter([
    {path: "/", element: <DashboardPage/>},
    {path: "/adhoc", element: <AdhocPage/>},
    {path: "/sign-in", element: <SignInPage/>},
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Flowbite theme={{theme: Theme}}>
            <RouterProvider router={router}/>
        </Flowbite>
    </React.StrictMode>,
)


import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Flowbite} from 'flowbite-react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

import DashboardPage from "./app/page/dashboard";
import HomePage from "./landing/page/home";
import SignInPage from "./landing/page/sign-in";
import SignUpPage from "./landing/page/sign-up";

const router = createBrowserRouter([
    { path: "/", element: <HomePage/> },
    { path: "/sign-in", element: <SignInPage/> },
    { path: "/sign-up", element: <SignUpPage/> },
    { path: "/dashboard", element: <DashboardPage/> }
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Flowbite theme={{theme: Theme}}>
            <RouterProvider router={router}/>
        </Flowbite>
    </React.StrictMode>,
)


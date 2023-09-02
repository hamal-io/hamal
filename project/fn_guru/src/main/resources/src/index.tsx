// =========================================================
// * Volt React Dashboard
// =========================================================
// * Product Page: https://themesberg.com/product/dashboard/volt-react
// * Copyright 2021 Themesberg (https://www.themesberg.com)
// * Official Repository: https://github.com/themesberg/volt-react-dashboard
// * License: MIT License (https://themesberg.com/licensing)
// * Designed and coded by https://themesberg.com
// * Modified by Hamal Devs 2023 (https://hamal.io)
// =========================================================
// * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. Please contact us to request a removal.

import React from 'react';

import "./scss/volt.scss";

import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {createRoot} from 'react-dom/client';
import HomePage from "./pages/home";
import Signin from "./pages/sign-in";
import Signup from "./pages/sign-up";
import Toc from "./pages/toc";
import DashboardPage from "./pages/dashboard";
import FunctionListPage from "./pages/function/list";
import {RouteWithSidebar} from "./pages/components/route";

const container = document.getElementById('root');
const root = createRoot(container!);

const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/sign-in", element: <Signin/>},
    {path: "/sign-up", element: <Signup/>},
    {path: "/toc", element: <Toc/>},
    {path: "/dashboard", element: <RouteWithSidebar component={<DashboardPage/>}/>},
    {path: "/functions", element: <RouteWithSidebar component={<FunctionListPage/>}/>},
]);

root.render(
    <>
        <RouterProvider router={router}/>
    </>
);

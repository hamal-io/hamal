// =========================================================
// * Product Page: https://themesberg.com/product/dashboard/volt-react
// * Copyright 2021 Themesberg (https://www.themesberg.com)
// * Official Repository: https://github.com/themesberg/volt-react-dashboard
// * License: MIT License (https://themesberg.com/licensing)
// * Designed and coded by https://themesberg.com
// =========================================================
// * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. Please contact us to request a removal.

import React, {StrictMode} from 'react';
import {createBrowserRouter, HashRouter, RouterProvider} from "react-router-dom";
import {createRoot} from 'react-dom/client';

// core styles
import "./scss/volt.scss";
import AdhocPage from "./page/adhoc";
import {RouteWithSidebar} from "./component/route";
import ExecutionListPage from "./page/execution";
import DashboardPage from "./page/dashboard";

import * as serviceWorker from "./serviceWorker";

const container = document.getElementById('root');
const root = createRoot(container!);

const router = createBrowserRouter([
    {path: "/", element: <RouteWithSidebar component={<DashboardPage/>}></RouteWithSidebar>,},
    {path: "/adhoc", element: <RouteWithSidebar component={<AdhocPage/>}></RouteWithSidebar>,},
    {path: "/executions", element: <RouteWithSidebar component={<ExecutionListPage/>}></RouteWithSidebar>,},
]);


root.render(
    <StrictMode>
        <RouterProvider router={router}/>
    </StrictMode>
);

serviceWorker.unregister();

// =========================================================
// * Product Page: https://themesberg.com/product/dashboard/volt-react
// * Copyright 2021 Themesberg (https://www.themesberg.com)
// * Official Repository: https://github.com/themesberg/volt-react-dashboard
// * License: MIT License (https://themesberg.com/licensing)
// * Designed and coded by https://themesberg.com
// =========================================================
// * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. Please contact us to request a removal.

import React from 'react';
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {createRoot} from 'react-dom/client';

// core styles
import "./scss/volt.scss";
import {RouteWithNavbar} from "./component/route";

import AdhocPage from "./page/adhoc";
import DashboardPage from "./page/dashboard";
import ExecutionListPage from "./page/execution-list";
import ExecutionDetailPage from "./page/execution-detail";
import LogsPage from "./page/log-list";
import FunctionListPage from "./page/function-list";
import FunctionDetailPage from "./page/function-detail";
import SettingsPage from "./page/settings";
import TriggerListPage from "./page/trigger-list";
import TriggerDetailPage from "./page/trigger-detail";
import TopicListPage from "./page/topic-list";
import TopicDetailPage from "./page/topic-detail";

const container = document.getElementById('root');
const root = createRoot(container!);

const router = createBrowserRouter([
    {path: "/", element: <RouteWithNavbar component={<DashboardPage/>}></RouteWithNavbar>,},
    {path: "/adhoc", element: <RouteWithNavbar component={<AdhocPage/>}></RouteWithNavbar>,},
    {path: "/executions", element: <RouteWithNavbar component={<ExecutionListPage/>}></RouteWithNavbar>,},
    {path: "/executions/:execId", element: <RouteWithNavbar component={<ExecutionDetailPage/>}></RouteWithNavbar>,},
    {path: "/log-list", element: <RouteWithNavbar component={<LogsPage/>}></RouteWithNavbar>,},
    {path: "/functions", element: <RouteWithNavbar component={<FunctionListPage/>}></RouteWithNavbar>,},
    {path: "/functions/:funcId", element: <RouteWithNavbar component={<FunctionDetailPage/>}></RouteWithNavbar>,},
    {path: "/settings", element: <RouteWithNavbar component={<SettingsPage/>}></RouteWithNavbar>,},
    {path: "/topics", element: <RouteWithNavbar component={<TopicListPage/>}></RouteWithNavbar>,},
    {path: "/topics/:topicId", element: <RouteWithNavbar component={<TopicDetailPage/>}></RouteWithNavbar>,},
    {path: "/triggers", element: <RouteWithNavbar component={<TriggerListPage/>}></RouteWithNavbar>,},
    {path: "/triggers/:funcId", element: <RouteWithNavbar component={<TriggerDetailPage/>}></RouteWithNavbar>,},
]);


root.render(
    <RouterProvider router={router}/>
);

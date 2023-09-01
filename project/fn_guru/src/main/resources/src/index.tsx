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

import LandingPage from "./page/landing";

const container = document.getElementById('root');
const root = createRoot(container!);

const router = createBrowserRouter([
    {path: "/", element: <RouteWithNavbar component={<LandingPage/>}></RouteWithNavbar>,},
]);

root.render(
    <RouterProvider router={router}/>
);

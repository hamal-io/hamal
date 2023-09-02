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
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {createRoot} from 'react-dom/client';
import HomePage from "./pages/HomePage";
import Signin from "./pages/Signin";

import "./scss/volt.scss";

const container = document.getElementById('root');
const root = createRoot(container!);

const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/sign-in", element: <Signin/>},
]);

root.render(
    <>
        <RouterProvider router={router}/>
    </>
);

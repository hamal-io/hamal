// =========================================================
// * Volt React Dashboard
// =========================================================

// * Product Page: https://themesberg.com/product/dashboard/volt-react
// * Copyright 2021 Themesberg (https://www.themesberg.com)
// * Official Repository: https://github.com/themesberg/volt-react-dashboard
// * License: MIT License (https://themesberg.com/licensing)
// * Modified: Hamal (https://hamal.io)

// * Designed and coded by https://themesberg.com

// =========================================================
// * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. Please contact us to request a removal.

import React from 'react';

// core styles
import "./scss/volt.scss";

// vendor styles
import "react-datetime/css/react-datetime.css";

import Presentation from "./pages/Presentation";
// import ScrollToTop from "./components/ScrollToTop";
//
// ReactDOM.render(
//   <HashRouter>
//     <ScrollToTop />
//     <HomePage />
//   </HashRouter>,
//   document.getElementById("root")
// );


import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {createRoot} from 'react-dom/client';

// core styles
import "./scss/volt.scss";

const container = document.getElementById('root');
const root = createRoot(container!);

const router = createBrowserRouter([
    {path: "/", element: <Presentation/>},
]);

root.render(
    <>
        <RouterProvider router={router}/>
    </>
);

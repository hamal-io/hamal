import React from "react";
import {createBrowserRouter} from "react-router-dom";

// public
import HomePage from "./pages/landing/home";

// app
import DashboardPage from "./pages/app/dashboard";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/app", element: <DashboardPage/>},
]);

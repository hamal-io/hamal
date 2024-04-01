import React from "react";
import {createBrowserRouter} from "react-router-dom";
import './global.css'

// public
import LandingPage from "./pages/landing";
import NotFoundPage from "@/app/not-found.tsx";
import RootLayout from "@/app/root-layout.tsx";
import DashboardPage from "@/pages/dashboard";

export const router = createBrowserRouter([
    {
        path: "/", element:
            <RootLayout>
                <LandingPage/>
            </RootLayout>
    },

    {
        path: "/dashboard", element:
            <RootLayout>
                <DashboardPage/>
            </RootLayout>
    },
    {path: "*", element: <NotFoundPage/>}
]);
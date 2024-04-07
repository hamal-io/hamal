import React from "react";
import {createBrowserRouter} from "react-router-dom";
import './global.css'

// public
import LandingPage from "./pages/landing";
import NotFoundPage from "@/app/not-found.tsx";
import RootLayout from "@/app/root-layout.tsx";
import DashboardPage from "@/pages/dashboard";
import {TestPage} from "@/pages/test"

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

    {
        path: "/test", element:
            <RootLayout>
                <TestPage/>
            </RootLayout>
    },
    {path: "*", element: <NotFoundPage/>}
]);

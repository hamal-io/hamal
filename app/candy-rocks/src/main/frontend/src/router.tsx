import React from "react";
import {createBrowserRouter} from "react-router-dom";
import './global.css'

// public
import LandingPage from "./pages/landing";
import RootLayout from "@/app/layout.tsx";
import NotFoundPage from "@/app/not-found.tsx";

export const router = createBrowserRouter([
    {path: "/", element:
        <RootLayout>
            <LandingPage/>
        </RootLayout>
    },

    {path: "/dashboad", element: <NotFoundPage/>}
]);

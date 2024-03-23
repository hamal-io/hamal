import React from "react";
import {createBrowserRouter} from "react-router-dom";
import './global.css'

// public
import LandingPage from "./pages/landing";
import RootLayout from "@/app/layout.tsx";
import NotFoundPage from "@/app/not-found.tsx";
import CardLarge1 from "@/components/template/CardLarge1/CardLarge1.tsx";

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
               <CardLarge1></CardLarge1>
            </RootLayout>
    },
    {path: "*", element: <NotFoundPage/>}
]);

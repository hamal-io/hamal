import React from "react";
import {createBrowserRouter} from "react-router-dom";
import './global.css'

import LandingPage from "./pages/landing";
import NotFoundPage from "@/app/not-found.tsx";
import RootLayout from "@/app/root-layout.tsx";
import {TestPage} from "@/pages/test"
import FlowListPage from "@/pages/app/flow-list";
import FlowDetailPage from "@/pages/app/flow-detail";
import RecipeListPage from "@/pages/app/recipe-list";
import RecipeDetailPage from "@/pages/app/recipe-detail";
import NodesEditor from "@/pages/app/nodes-editor";

export const router = createBrowserRouter([
    {
        path: "/", element:
            <RootLayout>
                <LandingPage/>
            </RootLayout>
    },
    {
        path: "/flows", element:
            <RootLayout>
                <FlowListPage/>
            </RootLayout>
    },
    {
        path: "/flows/:flowId", element:
            <RootLayout>
                <FlowDetailPage/>
            </RootLayout>
    },
    {
        path: "/editor", element:
        //<RootLayout>
            <NodesEditor/>
        //</RootLayout>
    },
    {
        path: "/recipes", element:
            <RootLayout>
                <RecipeListPage/>
            </RootLayout>
    },
    {
        path: "recipes/:recipeId", element:
            <RootLayout>
                <RecipeDetailPage/>
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

import {createBrowserRouter} from "react-router-dom";

// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Dashboard from "./pages/app/dashboard";
import EndpointListPage from "@/pages/app/endpoint-list";
import ExecDetailPage from "@/pages/app/exec-detail";
import ExecListPage from "@/pages/app/exec-list";
import FuncListPage from "@/pages/app/func-list";
import FuncDetailPage from "@/pages/app/func-detail";
import HookListPage from "@/pages/app/hook-list";
import ScheduleListPage from "@/pages/app/schedule-list";
import AdhocPage from "@/pages/app/adhoc";


import WorkspaceLayout from "./components/app/layout/workspace";
import WorkspaceNamespaceListTab from "./pages/app/workspace-detail/tab/namespace-list";
import TopicListPage from "@/pages/app/topic-list";
import TopicDetailPage from "@/pages/app/topic-detail";
import React from "react";
import WorkspaceDetailPage from "@/pages/app/workspace-detail";
import WorkspaceGeneralTab from "@/pages/app/workspace-detail/tab/general";
import AccountPage from "@/pages/app/account/account.tsx";
import RecipeEditor from "@/pages/app/recipe-list/components/editor.tsx";
import RecipePage from "@/pages/app/recipe-list";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },
    {
        path: "/account", element:
            <WorkspaceLayout>
                <AccountPage/>
            </WorkspaceLayout>
    },
    {
        path: "/recipes/editor/:recipeId", element:
            <WorkspaceLayout>
                <RecipeEditor/>
            </WorkspaceLayout>
    },
    {
        path: "/recipes", element:
            <WorkspaceLayout>
                <RecipePage/>
            </WorkspaceLayout>
    },
    {
        path: "/dashboard", element:
            <WorkspaceLayout>
                <Dashboard/>
            </WorkspaceLayout>
    },
    {
        path: "/adhoc", element:
            <WorkspaceLayout>
                <AdhocPage/>
            </WorkspaceLayout>
    },
    {
        path: "/executions", element:
            <WorkspaceLayout>
                <ExecListPage/>
            </WorkspaceLayout>
    },
    {
        path: "/executions/:execId", element:
            <WorkspaceLayout>
                <ExecDetailPage/>
            </WorkspaceLayout>
    },
    {
        path: "/functions", element:
            <WorkspaceLayout>
                <FuncListPage/>
            </WorkspaceLayout>
    },
    {
        path: "/functions/:funcId", element:
            <WorkspaceLayout>
                <FuncDetailPage/>
            </WorkspaceLayout>
    },
    {
        path: "/webhooks", element:
            <WorkspaceLayout>
                <HookListPage/>
            </WorkspaceLayout>
    },
    {
        path: "/endpoints", element:
            <WorkspaceLayout>
                <EndpointListPage/>
            </WorkspaceLayout>
    },
    {
        path: "/schedules", element:
            <WorkspaceLayout>
                <ScheduleListPage/>
            </WorkspaceLayout>
    },
    {
        path: "/namespaces", element:
            <WorkspaceLayout>
                <WorkspaceNamespaceListTab/>
            </WorkspaceLayout>
    },
    {
        path: "/topics", element:
            <WorkspaceLayout>
                <TopicListPage/>
            </WorkspaceLayout>
    },
    {
        path: "/topics/:topicId", element:
            <WorkspaceLayout>
                <TopicDetailPage/>
            </WorkspaceLayout>
    },
    {
        path: "/workspace", element:
            <WorkspaceLayout>
                <WorkspaceDetailPage>
                    <WorkspaceGeneralTab/>
                </WorkspaceDetailPage>
            </WorkspaceLayout>
    },
    {
        path: "/workspace/namespaces", element:
            <WorkspaceLayout>
                <WorkspaceDetailPage>
                    <WorkspaceNamespaceListTab/>
                </WorkspaceDetailPage>
            </WorkspaceLayout>
    }
]);

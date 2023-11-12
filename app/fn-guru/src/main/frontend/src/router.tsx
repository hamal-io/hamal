// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Authenticated from "@/components/app/authenticated";

import Dashboard from "./pages/app/dashboard";

import FlowListPage from "./pages/app/flow-list";
import FlowDetailPage from "./pages/app/flow-detail";

import FlowOverviewPage from "@/pages/app/flow-detail/pages/overview";
import FlowExecDetailPage from "@/pages/app/flow-detail/pages/exec-detail";
import FlowExecListPage from "@/pages/app/flow-detail/pages/exec-list";
import FlowFuncListPage from "@/pages/app/flow-detail/pages/func-list";
import FlowFuncDetailPage from "@/pages/app/flow-detail/pages/func-detail";
import FlowScheduleListPage from "@/pages/app/flow-detail/pages/schedule-list";

import {createBrowserRouter} from "react-router-dom";
import Playground from "@/pages/app/playground";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },
    {
        path: "/dashboard", element:
            <Authenticated>
                <Dashboard/>
            </Authenticated>
    },
    {
        path: "/flows", element:
            <Authenticated>
                <FlowListPage/>
            </Authenticated>
    },
    {
        path: "/flows/:flowId", element:
            <FlowDetailPage>
                <FlowOverviewPage/>
            </FlowDetailPage>
    },
    {
        path: "/flows/:flowId/executions", element:
            <FlowDetailPage>
                <FlowExecListPage/>
            </FlowDetailPage>
    },
    {
        path: "/flows/:flowId/executions/:execId", element:
            <FlowDetailPage>
                <FlowExecDetailPage/>
            </FlowDetailPage>
    },
    {
        path: "/flows/:flowId/functions", element:
            <FlowDetailPage>
                <FlowFuncListPage/>
            </FlowDetailPage>
    },
    {
        path: "/flows/:flowId/functions/:funcId", element:
            <FlowDetailPage>
                <FlowFuncDetailPage/>
            </FlowDetailPage>
    },
    {
        path: "/flows/:flowId/schedules", element:
            <FlowDetailPage>
                <FlowScheduleListPage/>
            </FlowDetailPage>
    },

    {
        path: "/playground", element:
            <Authenticated>
                <Playground/>
            </Authenticated>
    }
]);

// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Authenticated from "@/components/app/authenticated";
import Dashboard from "./pages/app/dashboard";

import FlowListPage from "./pages/app/flow-list";
import FlowDetailPage from "./pages/app/flow-detail";

import EndpointListPage from "@/pages/app/endpoint-list";
import ExecDetailPage from "@/pages/app/exec-detail";
import ExecListPage from "@/pages/app/exec-list";
import FuncListPage from "@/pages/app/func-list";
import FuncDetailPage from "@/pages/app/func-detail";
import HookListPage from "@/pages/app/hook-list";
import FlowscheduleListPage from "@/pages/app/schedule-list";

import {createBrowserRouter} from "react-router-dom";
import Playground from "@/pages/app/playground";
import GroupLayout from "@/components/app/layout/group";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },
    {
        path: "/groups", element:
            <Authenticated>
                <FlowListPage/>
            </Authenticated>
    },
    {
        path: "/groups/:groupId/dashboard", element:
            <GroupLayout>
                <Dashboard/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/playground", element:
            <GroupLayout>
                <Playground/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/executions", element:
            <GroupLayout>
                <ExecListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/executions/:execId", element:
            <GroupLayout>
                <ExecDetailPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/functions", element:
            <GroupLayout>
                <FuncListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/functions/:funcId", element:
            <GroupLayout>
                <FuncDetailPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/webhooks", element:
            <GroupLayout>
                <HookListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/endpoints", element:
            <GroupLayout>
                <EndpointListPage/>
            </GroupLayout>
    },
    // {
    //     path: "/groups/:groupId/schedules", element:
    //         <GroupLayout>
    //             <FlowscheduleListPage/>
    //         </GroupLayout>
    // }
]);

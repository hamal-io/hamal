// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Index from "@/components/app/layout/authenticated";
import Dashboard from "./pages/app/dashboard";

import FlowListPage from "./pages/app/flow-list";
import FlowDetailPage from "./pages/app/flow-detail";

import EndpointListPage from "@/pages/app/endpoint-list";
import ExecDetailPage from "@/pages/app/exec-detail";
import ExecListPage from "@/pages/app/exec-list";
import FuncListPage from "@/pages/app/func-list";
import FuncDetailPage from "@/pages/app/func-detail";
import HookListPage from "@/pages/app/hook-list";
import ScheduleListPage from "@/pages/app/schedule-list";

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
            <Index>
                <FlowListPage/>
            </Index>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/dashboard", element:
            <GroupLayout>
                <Dashboard/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/playground", element:
            <GroupLayout>
                <Playground/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/executions", element:
            <GroupLayout>
                <ExecListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/executions/:execId", element:
            <GroupLayout>
                <ExecDetailPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/functions", element:
            <GroupLayout>
                <FuncListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/functions/:funcId", element:
            <GroupLayout>
                <FuncDetailPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/webhooks", element:
            <GroupLayout>
                <HookListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/endpoints", element:
            <GroupLayout>
                <EndpointListPage/>
            </GroupLayout>
    },
    {
        path: "/groups/:groupId/namespaces/:namespaceId/schedules", element:
            <GroupLayout>
                <ScheduleListPage/>
            </GroupLayout>
    }
]);

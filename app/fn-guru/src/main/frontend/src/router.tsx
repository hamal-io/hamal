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
import Playground from "@/pages/app/playground";

import WorkspaceLayout from "./components/app/layout/workspace";
import NamespaceListPage from "./pages/app/namespace-list";
import TopicListPage from "@/pages/app/topic-list";
import TopicDetailPage from "@/pages/app/topic-detail";
import BlueprintListPage from "@/pages/app/blueprint-list";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },
    {
        path: "/blueprints", element:
            <GroupLayout>
                <BlueprintListPage/>
            </GroupLayout>
    },
    {
        path: "/dashboard", element:
            <WorkspaceLayout>
                <Dashboard/>
            </WorkspaceLayout>
    },
    {
        path: "/playground", element:
            <WorkspaceLayout>
                <Playground/>
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
                <NamespaceListPage/>
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
    }
]);

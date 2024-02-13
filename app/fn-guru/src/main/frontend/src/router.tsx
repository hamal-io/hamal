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
import GroupLayout from "@/components/app/layout/group";
import NamespaceListPage from "./pages/app/namespace-list";
import TopicListPage from "@/pages/app/topic-list";
import TopicDetailPage from "@/pages/app/topic-detail";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },
    {
        path: "/blueprints", element:
            <GroupLayout>
                {/*<BlueprintListPage> */}
            </GroupLayout>
    },
    {
        path: "/blueprints/:blueprintId", element:
            <GroupLayout>
                {/*<BlueprintDetailPage> */}
            </GroupLayout>
    },
    {
        path: "/dashboard", element:
            <GroupLayout>
                <Dashboard/>
            </GroupLayout>
    },
    {
        path: "/playground", element:
            <GroupLayout>
                <Playground/>
            </GroupLayout>
    },
    {
        path: "/executions", element:
            <GroupLayout>
                <ExecListPage/>
            </GroupLayout>
    },
    {
        path: "/executions/:execId", element:
            <GroupLayout>
                <ExecDetailPage/>
            </GroupLayout>
    },
    {
        path: "/functions", element:
            <GroupLayout>
                <FuncListPage/>
            </GroupLayout>
    },
    {
        path: "/functions/:funcId", element:
            <GroupLayout>
                <FuncDetailPage/>
            </GroupLayout>
    },
    {
        path: "/webhooks", element:
            <GroupLayout>
                <HookListPage/>
            </GroupLayout>
    },
    {
        path: "/endpoints", element:
            <GroupLayout>
                <EndpointListPage/>
            </GroupLayout>
    },
    {
        path: "/schedules", element:
            <GroupLayout>
                <ScheduleListPage/>
            </GroupLayout>
    },
    {
        path: "/namespaces", element:
            <GroupLayout>
                <NamespaceListPage/>
            </GroupLayout>
    },
    {
        path: "/topics", element:
            <GroupLayout>
                <TopicListPage/>
            </GroupLayout>
    },
    {
        path: "/topics/:topicId", element:
            <GroupLayout>
                <TopicDetailPage/>
            </GroupLayout>
    }
]);

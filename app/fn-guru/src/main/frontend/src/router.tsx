// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Authenticated from "@/components/app/authenticated";

import Dashboard from "./pages/app/dashboard";

import NamespaceListPage from "./pages/app/namespace-list";
import NamespaceDetailPage from "./pages/app/namespace-detail";

import NamespaceOverviewPage from "@/pages/app/namespace-detail/pages/overview";
import NamespaceEndpointListPage from "@/pages/app/namespace-detail/pages/endpoint-list";
import NamespaceExecDetailPage from "@/pages/app/namespace-detail/pages/exec-detail";
import NamespaceExecListPage from "@/pages/app/namespace-detail/pages/exec-list";
import NamespaceFuncListPage from "@/pages/app/namespace-detail/pages/func-list";
import NamespaceFuncDetailPage from "@/pages/app/namespace-detail/pages/func-detail";
import NamespaceHookListPage from "@/pages/app/namespace-detail/pages/hook-list";
import NamespacescheduleListPage from "@/pages/app/namespace-detail/pages/schedule-list";

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
        path: "/namespaces", element:
            <Authenticated>
                <NamespaceListPage/>
            </Authenticated>
    },
    {
        path: "/namespaces/:namespaceId", element:
            <NamespaceDetailPage>
                <NamespaceOverviewPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/executions", element:
            <NamespaceDetailPage>
                <NamespaceExecListPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/executions/:execId", element:
            <NamespaceDetailPage>
                <NamespaceExecDetailPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/functions", element:
            <NamespaceDetailPage>
                <NamespaceFuncListPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/functions/:funcId", element:
            <NamespaceDetailPage>
                <NamespaceFuncDetailPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/hooks", element:
            <NamespaceDetailPage>
                <NamespaceHookListPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/endpoints", element:
            <NamespaceDetailPage>
                <NamespaceEndpointListPage/>
            </NamespaceDetailPage>
    },
    {
        path: "/namespaces/:namespaceId/schedules", element:
            <NamespaceDetailPage>
                <NamespacescheduleListPage/>
            </NamespaceDetailPage>
    },

    {
        path: "/playground", element:
            <Authenticated>
                <Playground/>
            </Authenticated>
    }
]);

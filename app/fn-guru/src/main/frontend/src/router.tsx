// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import PlayPage from "./pages/app/adhoc";
import Authenticated from "@/components/app/authenticated";

import NamespaceListPage from "./pages/app/namespace-list";
import NamespaceDetailPage from "./pages/app/namespace-detail";

import NamespaceDashboardPage from "@/pages/app/namespace-detail/page/dashboard";
import NamespaceExecDetailPage from "@/pages/app/namespace-detail/page/exec-detail";
import NamespaceExecListPage from "@/pages/app/namespace-detail/page/exec-list";
import NamespaceFuncDetail from "@/pages/app/namespace-detail/page/func-detail";
import NamespaceFuncListPage from "@/pages/app/namespace-detail/page/func-list";

import {createBrowserRouter} from "react-router-dom";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },

    {
        path: "/namespaces", element:
            <Authenticated>
                <NamespaceListPage/>
            </Authenticated>
    },
    {
        path: "/namespaces/:namespaceId", element:
            <Authenticated>
                <NamespaceDetailPage>
                    <NamespaceDashboardPage/>
                </NamespaceDetailPage>
            </Authenticated>
    },

    {
        path: "/namespaces/:namespaceId/executions", element:
            <Authenticated>
                <NamespaceDetailPage>
                    <NamespaceExecListPage/>
                </NamespaceDetailPage>
            </Authenticated>
    },

    {
        path: "/namespaces/:namespaceId/executions/:execId", element:
            <Authenticated>
                <NamespaceDetailPage>
                    <NamespaceExecDetailPage/>
                </NamespaceDetailPage>
            </Authenticated>
    },

    {
        path: "/namespaces/:namespaceId/functions", element:
            <Authenticated>
                <NamespaceDetailPage>
                    <NamespaceFuncListPage/>
                </NamespaceDetailPage>
            </Authenticated>
    },

    {
        path: "/namespaces/:namespaceId/functions/:funcId", element:
            <Authenticated>
                <NamespaceDetailPage>
                    <NamespaceFuncDetail/>
                </NamespaceDetailPage>
            </Authenticated>
    },

    {
        path: "/play", element:
            <Authenticated>
                <PlayPage/>
            </Authenticated>
    }
]);

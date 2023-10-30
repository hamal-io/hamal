// public
import HomePage from "./public/page/home";
import LoginInPage from "./public/page/login";

// app
import PlayPage from "./app/page/adhoc";
import Authenticated from "./app/component/authenticated";

import NamespaceListPage from "./app/page/namespace-list";
import NamespaceDetailPage from "./app/page/namespace-detail";

import NamespaceDashboardPage from "./app/page/namespace-detail/page/dashboard";
import NamespaceExecDetailPage from "./app/page/namespace-detail/page/exec-detail";
import NamespaceExecListPage from "./app/page/namespace-detail/page/exec-list";
import NamespaceFuncDetail from "./app/page/namespace-detail/page/func-detail";
import NamespaceFuncListPage from "./app/page/namespace-detail/page/func-list";

import OnboardingPage from "./public/page/onboarding";
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

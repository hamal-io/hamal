// public
import HomePage from "./public/page/home";
import LoginInPage from "./public/page/login";

// app
import AdhocPage from "./app/page/adhoc";
import AuthenticatedPage from "./app/page/authenticated";

import NamespaceListPage from "./app/page/namespace-list";
import NamespaceDetailPage from "./app/page/namespace-detail";

import NamespaceDashboardPage from "./app/page/dashboard";
import NamespaceExecDetailPage from "./app/page/exec-detail";
import NamespaceExecListPage from "./app/page/exec-list";
import NamespaceFuncDetail from "./app/page/func-detail";
import NamespaceFuncListPage from "./app/page/func-list";

import OnboardingPage from "./public/page/onboarding";
import {createBrowserRouter} from "react-router-dom";


export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginInPage/>},

    {
        path: "/onboarding", element: <OnboardingPage/>
    },

    {path: "/namespaces", element: <AuthenticatedPage><NamespaceListPage/></AuthenticatedPage>},
    {
        path: "/namespaces/:namespaceId", element: <AuthenticatedPage>
            <NamespaceDetailPage>
                <NamespaceDashboardPage/>
            </NamespaceDetailPage>
        </AuthenticatedPage>
    },

    {
        path: "/namespaces/:namespaceId/executions", element: <AuthenticatedPage>
            <NamespaceDetailPage>
                <NamespaceExecListPage/>
            </NamespaceDetailPage>
        </AuthenticatedPage>
    },

    {
        path: "/namespaces/:namespaceId/executions/:execId", element: <AuthenticatedPage>
            <NamespaceDetailPage>
                <NamespaceExecDetailPage/>
            </NamespaceDetailPage>
        </AuthenticatedPage>
    },

    {
        path: "/namespaces/:namespaceId/functions", element: <AuthenticatedPage>
            <NamespaceDetailPage>
                <NamespaceFuncListPage/>
            </NamespaceDetailPage>
        </AuthenticatedPage>
    },

    {
        path: "/namespaces/:namespaceId/functions/:funcId", element: <AuthenticatedPage>
            <NamespaceDetailPage>
                <NamespaceFuncDetail/>
            </NamespaceDetailPage>
        </AuthenticatedPage>
    },

    {path: "/play", element: <AuthenticatedPage><AdhocPage/></AuthenticatedPage>}
]);

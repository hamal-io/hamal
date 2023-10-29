// public
import HomePage from "./public/page/home";
import LoginInPage from "./public/page/login";

// private
import AdhocPage from "./private/page/adhoc";
import AuthenticatedPage from "./private/page/authenticated";

import NamespaceListPage from "./private/page/namespace-list";
import NamespaceDetailPage from "./private/page/namespace-detail";

import NamespaceDashboardPage from "./private/page/namespace-detail/page/dashboard";
import NamespaceExecDetailPage from "./private/page/namespace-detail/page/exec-detail";
import NamespaceExecListPage from "./private/page/namespace-detail/page/exec-list";
import NamespaceFuncDetail from "./private/page/namespace-detail/page/func-detail";
import NamespaceFuncListPage from "./private/page/namespace-detail/page/func-list";

import OnboardingPage from "./private/page/onboarding";
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

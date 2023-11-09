// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Authenticated from "@/components/app/authenticated";

import Dashboard from "./pages/app/dashboard";

import NamespaceList from "./pages/app/namespace-list";
import NamespaceDetail from "./pages/app/namespace-detail";

import NamespaceDashboardPage from "@/pages/app/namespace-detail/page/dashboard";
import NamespaceExecDetailPage from "@/pages/app/namespace-detail/page/exec-detail";
import NamespaceExecListPage from "@/pages/app/namespace-detail/page/exec-list";
import NamespaceFuncDetail from "@/pages/app/namespace-detail/page/func-detail";
// import NamespaceFuncListPage from "@/pages/app/namespace-detail/page/func-list";

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
                <NamespaceList/>
            </Authenticated>
    },
    {
        path: "/namespaces/:namespaceId", element:
            <Authenticated>
                <NamespaceDetail>
                    <NamespaceDashboardPage/>
                </NamespaceDetail>
            </Authenticated>
    },

    {
        path: "/namespaces/:namespaceId/executions", element:
            <Authenticated>
                <NamespaceDetail>
                    <NamespaceExecListPage/>
                </NamespaceDetail>
            </Authenticated>
    },

    {
        path: "/namespaces/:namespaceId/executions/:execId", element:
            <Authenticated>
                <NamespaceDetail>
                    <NamespaceExecDetailPage/>
                </NamespaceDetail>
            </Authenticated>
    },

    // {
    //     path: "/namespaces/:namespaceId/functions", element:
    //         <Authenticated>
    //             <NamespaceDetail>
    //                 <NamespaceFuncListPage/>
    //             </NamespaceDetail>
    //         </Authenticated>
    // },

    {
        path: "/namespaces/:namespaceId/functions/:funcId", element:
            <Authenticated>
                <NamespaceDetail>
                    <NamespaceFuncDetail/>
                </NamespaceDetail>
            </Authenticated>
    },

    {
        path: "/playground", element:
            <Authenticated>
                <Playground/>
            </Authenticated>
    }
]);

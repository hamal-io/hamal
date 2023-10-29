import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Flowbite} from 'flowbite-react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

// public
import HomePage from "./landing/page/home";
import SignInPage from "./landing/page/sign-in";
import SignUpPage from "./landing/page/sign-up";

// app
import AdhocPage from "./app/desktop/page/adhoc";
import AuthenticatedPage from "./app/desktop/page/authenticated";

import NamespaceListPage from "./app/desktop/page/namespace-list";
import NamespaceDetailPage from "./app/desktop/page/namespace-detail";

import NamespaceDashboardPage from "./app/desktop/page/namespace-detail/page/dashboard";
import NamespaceExecDetailPage from "./app/desktop/page/namespace-detail/page/exec-detail";
import NamespaceExecListPage from "./app/desktop/page/namespace-detail/page/exec-list";
import NamespaceFuncDetail from "./app/desktop/page/namespace-detail/page/func-detail";
import NamespaceFuncListPage from "./app/desktop/page/namespace-detail/page/func-list";

import OnboardingPage from "./app/desktop/page/onboarding";


const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/sign-in", element: <SignInPage/>},
    {path: "/sign-up", element: <SignUpPage/>},

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

ReactDOM.createRoot(document.getElementById('root')!).render(
    <Flowbite theme={{theme: Theme}}>
        <RouterProvider router={router}/>
    </Flowbite>
)


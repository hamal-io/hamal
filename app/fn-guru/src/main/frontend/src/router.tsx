// public
import HomePage from "./pages/landing/home";
import LoginInPage from "./pages/landing/login";
import OnboardingPage from "./pages/landing/onboarding";

// app
import Authenticated from "@/components/app/authenticated";

import Dashboard from "./pages/app/dashboard";

import FlowList from "./pages/app/flow-list";
import FlowDetail from "./pages/app/flow-detail";

import FlowDashboardPage from "@/pages/app/flow-detail/page/dashboard";
import FlowExecDetailPage from "@/pages/app/flow-detail/page/exec-detail";
import FlowExecListPage from "@/pages/app/flow-detail/page/exec-list";
import FlowFuncDetail from "@/pages/app/flow-detail/page/func-detail";
// import FlowFuncListPage from "@/pages/app/flow-detail/page/func-list";

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
        path: "/flows", element:
            <Authenticated>
                <FlowList/>
            </Authenticated>
    },
    {
        path: "/flows/:flowId", element:
            <Authenticated>
                <FlowDetail>
                    <FlowDashboardPage/>
                </FlowDetail>
            </Authenticated>
    },

    {
        path: "/flows/:flowId/executions", element:
            <Authenticated>
                <FlowDetail>
                    <FlowExecListPage/>
                </FlowDetail>
            </Authenticated>
    },

    {
        path: "/flows/:flowId/executions/:execId", element:
            <Authenticated>
                <FlowDetail>
                    <FlowExecDetailPage/>
                </FlowDetail>
            </Authenticated>
    },

    // {
    //     path: "/flows/:flowId/functions", element:
    //         <Authenticated>
    //             <FlowDetail>
    //                 <FlowFuncListPage/>
    //             </FlowDetail>
    //         </Authenticated>
    // },

    {
        path: "/flows/:flowId/functions/:funcId", element:
            <Authenticated>
                <FlowDetail>
                    <FlowFuncDetail/>
                </FlowDetail>
            </Authenticated>
    },

    {
        path: "/playground", element:
            <Authenticated>
                <Playground/>
            </Authenticated>
    }
]);

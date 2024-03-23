import React from 'react'
import ReactDOM from 'react-dom/client'
import {RouterProvider} from "react-router-dom";

import {router} from "./router.tsx";
import {TailwindIndicator} from "@/components/tailwind-indicator.tsx";
import Background from "@/app/background.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Background/>
            <RouterProvider router={router}/>
        <TailwindIndicator/>
    </React.StrictMode>
)


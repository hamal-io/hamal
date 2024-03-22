import React from 'react'
import ReactDOM from 'react-dom/client'
import {RouterProvider} from "react-router-dom";

import {router} from "./router.tsx";
import {TailwindIndicator} from "@/components/tailwind-indicator.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <RouterProvider router={router}/>
        <TailwindIndicator/>
    </React.StrictMode>
)


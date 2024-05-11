import React from 'react'
import ReactDOM from 'react-dom/client'
import {RouterProvider} from "react-router-dom";

import {router} from "./router.tsx";
import "@/styles/index.scss";
import "rc-slider/assets/index.css";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <div className="flex flex-col h-screen">
            <RouterProvider router={router}/>
        </div>
    </React.StrictMode>
)


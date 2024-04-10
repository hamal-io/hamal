import React from 'react'
import ReactDOM from 'react-dom/client'
import {RouterProvider} from "react-router-dom";

import {router} from "./router.tsx";
import {Background} from "@/app/background.tsx";
import "@/styles/index.scss";
import "rc-slider/assets/index.css";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Background>
            <RouterProvider router={router}/>
        </Background>
        {/*<TailwindIndicator/>*/}
    </React.StrictMode>
)


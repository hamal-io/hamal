import React from 'react'
import ReactDOM from 'react-dom/client'
import './global.css'
import {RouterProvider} from "react-router-dom";
import {MetaMaskProvider} from '@metamask/sdk-react';

import {router} from "./router.tsx";
import {TailwindIndicator} from "@/components/tailwind-indicator.tsx";
import Background from "@/components/background.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <MetaMaskProvider debug={false} sdkOptions={{
            checkInstallationImmediately: false,
            dappMetadata: {
                name: "fn(guru)",
                url: window.location.host,
            }
        }}>
            <Background>
                <RouterProvider router={router}/>
            </Background>
            <TailwindIndicator/>
        </MetaMaskProvider>
    </React.StrictMode>
)


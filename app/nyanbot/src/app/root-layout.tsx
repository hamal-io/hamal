import React, {FC, ReactNode, useEffect} from "react";
import {useUiState} from "@/hook/ui.ts";
import {MetaMaskProvider} from "@metamask/sdk-react";
import Header from "@/app/header";

type Props = {
    children: ReactNode;
}
const RootLayout: FC<Props> = ({children}) => {
    const [uiState] = useUiState()

    useEffect(() => {
        const root = document.querySelector("html");
        if (!root) return;
        if (uiState.theme === 'dark') {
            !root.classList.contains("dark") && root.classList.add("dark");
        } else {
            root.classList.remove("dark");
        }
    }, [uiState.theme]);

    return (
        <div className="flex-1 h-full">
            <MetaMaskProvider debug={false} sdkOptions={{
                checkInstallationImmediately: false,
                dappMetadata: {
                    name: "nyanbot.com",
                    url: window.location.host,
                }
            }}>
                <Header/>
                {children}
            </MetaMaskProvider>
        </div>
    )
}

export default RootLayout
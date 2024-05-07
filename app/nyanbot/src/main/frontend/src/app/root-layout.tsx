import {FC, ReactNode, useEffect} from "react";
import Header from "@/app/header/index.tsx";
import {useUiState} from "@/hook/ui.ts";
import {MetaMaskProvider} from "@metamask/sdk-react";

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
        <div>
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
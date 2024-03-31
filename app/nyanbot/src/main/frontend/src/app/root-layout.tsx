import {FC, ReactNode, useEffect} from "react";
import Header from "@/app/header/index.tsx";
import {useUiState} from "@/hooks/ui.ts";

type Props = {
    children: Iterable<ReactNode>;
}
const RootLayout: FC<Props> = ({children}) => {
    const [uiState] = useUiState()

    useEffect(() => {
        const root = document.querySelector("html");
        if (!root) return;
        if (uiState.theme !== 'dark') {
            !root.classList.contains("dark") && root.classList.add("dark");
        } else {
            root.classList.remove("dark");
        }
    }, [uiState.theme]);


    return (
        <div>
            <Header/>
            {children}
        </div>
    )
}

export default RootLayout
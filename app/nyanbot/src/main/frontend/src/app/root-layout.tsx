import {FC, ReactNode} from "react";
import Header from "@/app/header/index.tsx";

type Props = {
    children: Iterable<ReactNode>;
}
const RootLayout: FC<Props> = ({children}) => {
    return (
        <div>
            <Header/>
            {children}
        </div>
    )
}

export default RootLayout
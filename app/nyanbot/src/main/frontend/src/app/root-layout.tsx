import Footer from "@/app/Footer.tsx";
import {FC, ReactNode} from "react";
import SiteHeader from "@/app/header.tsx";


type Props = {
    children: Array<ReactNode>
}
const RootLayout: FC<Props> = ({children}) => {
    return (
        <div>
            <SiteHeader/>
            {children}
            <Footer/>
        </div>
    )
}

export default RootLayout
import Footer from "@/components/shared/Footer/Footer.tsx";
import {FC} from "react";
import SiteHeader from "@/app/header.tsx";

type Props = {
    children
}
const RootLayout: FC<Props> = ({children}) => {
    return (
        <>
            <SiteHeader/>
                {children}
            <Footer/>
        </>
    )
}

export default RootLayout
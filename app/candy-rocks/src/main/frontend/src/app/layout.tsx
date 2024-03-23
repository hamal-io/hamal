import SiteHeader from "@/app/SiteHeader.tsx";
import Footer from "@/components/shared/Footer/Footer.tsx";
import {FC} from "react";

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
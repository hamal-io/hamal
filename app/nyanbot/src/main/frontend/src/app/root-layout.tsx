import Footer from "@/components/ui/footer/Footer.tsx";
import {FC} from "react";
import SiteHeader from "@/app/header.tsx";


type Props = {
    children
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
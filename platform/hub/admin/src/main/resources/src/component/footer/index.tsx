import {Footer} from "flowbite-react";


export default function () {
    return (
        <Footer container>
            <Footer.Copyright
                by="hamal.io"
                href="#"
                year={2023}
            />
            <Footer.LinkGroup>
                <Footer.Link href="#">
                    About
                </Footer.Link>
                <Footer.Link href="#">
                    Privacy Policy
                </Footer.Link>
                <Footer.Link href="#">
                    Licensing
                </Footer.Link>
                <Footer.Link href="#">
                    Contact
                </Footer.Link>
            </Footer.LinkGroup>
        </Footer>
    )
}
import {Footer as Delegate} from "flowbite-react";
import {FC} from "react";

export const Footer: FC = () => (
    <Delegate container>
        <Delegate.Copyright
            by="fn.guru"
            href="#"
            year={2023}
        />
        <Delegate.LinkGroup>
            <Delegate.Link href="#">
                About
            </Delegate.Link>
            <Delegate.Link href="#">
                Privacy Policy
            </Delegate.Link>
            <Delegate.Link href="#">
                Licensing
            </Delegate.Link>
            <Delegate.Link href="#">
                Contact
            </Delegate.Link>
        </Delegate.LinkGroup>
    </Delegate>
);

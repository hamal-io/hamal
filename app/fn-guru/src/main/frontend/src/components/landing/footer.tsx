import React, {FC} from "react";
import {Link} from "react-router-dom";

const Footer: FC = () => (
    <footer className="mx-auto w-full max-w-4xl">
        <div className="grid grid-cols-2 gap-6 md:grid-cols-4">
            <div className="order-4 md:order-1">
                Powered by hamal.io
            </div>
            <div className="order-1 flex flex-col gap-3 text-sm md:order-2">
                <p className="text-foreground font-semibold">Community</p>
                <FooterLink href="https://github.com/hamal-io/hamal" label="GitHub"/>
            </div>
            <div className="order-2 flex flex-col gap-3 text-sm md:order-3">
                <p className="text-foreground font-semibold">Resources</p>
                <FooterLink href="https://docs.fn.guru" label="Documentation"/>
            </div>
            <div className="order-3 flex flex-col gap-3 text-sm md:order-4">
                <p className="text-foreground font-semibold">Legal</p>
                <FooterLink href="/legal/terms" label="Terms"/>
                <FooterLink href="/legal/privacy" label="Privacy"/>
            </div>
        </div>
    </footer>
);

export default Footer;

type FooterLinkProps = {
    href: string;
    label: string;
}

const FooterLink: FC<FooterLinkProps> = ({href, label}) => {
    return (
        <Link
            className="text-muted-foreground hover:text-foreground inline-flex items-center underline underline-offset-4 hover:no-underline"
            to={href}
            target="_blank"
            rel="noreferrer"
        >
            {label}
        </Link>
    );
}

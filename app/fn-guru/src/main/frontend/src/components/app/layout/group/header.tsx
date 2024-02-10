import React, {FC} from "react";
import {useAuth} from "@/hook/auth.ts";
import {Link, useLocation, useParams} from "react-router-dom";
import {cn} from "@/utils";

const GroupHeader: FC = () => {
    return (
        <div className="border-b bg-white">
            <div className="flex h-16 px-4">
                <div className="w-full flex items-center justify-between space-x-4">
                    <Nav className="mx-6"/>
                </div>
            </div>
        </div>
    )
}

export default GroupHeader;

const Nav = ({className, ...props}: React.HTMLAttributes<HTMLElement>) => {
    const location = useLocation()
    const currentPath = location.pathname

    const {groupId} = useParams()

    const navigation: NavItem[] = [
        {
            href: `/groups/${groupId}/dashboard`,
            label: "Dashboard",
            active: currentPath === `/groups/${groupId}/dashboard`
        },
        {
            href: `/groups/${groupId}/playground`,
            label: "Playground",
            active: currentPath === `/groups/${groupId}/playground`
        },
        {
            href: `/groups/${groupId}/executions`,
            label: "Executions",
            active: currentPath.startsWith(`/groups/${groupId}/executions`)
        },
        {
            href: `/groups/${groupId}/functions`,
            label: "Functions",
            active: currentPath.startsWith(`/groups/${groupId}/functions`)
        },
        {
            href: `/groups/${groupId}/schedules`,
            label: "Schedules",
            active: currentPath.startsWith(`/groups/${groupId}/schedules`)
        },
        // {
        //     href: `/groups/${groupId}/webhooks`,
        //     label: "Webhook",
        //     active: currentPath.startsWith(`/groups/${groupId}/webhooks`)
        // },
        {
            href: `/groups/${groupId}/endpoints`,
            label: "Endpoint",
            active: currentPath.startsWith(`/groups/${groupId}/endpoints`)
        },
        // {
        //     href: `/groups/${groupId}/triggers`,
        //     label: "Trigger",
        //     active: currentPath.startsWith(`/groups/${groupId}/triggers`)
        // },
        {
            href: "https://docs.fn.guru",
            external: true,
            label: "Documentation",
        },
    ];

    return (
        <nav className="flex items-center space-x-4 lg:space-x-6">
            {navigation.map((item) => (<NavLink key={item.label} item={item}/>))}
        </nav>
    )
}

type NavItem = {
    href: string;
    external?: boolean;
    label: string;
    active?: boolean;
};

const NavLink: FC<{
    item: NavItem
}> = ({item}) => {
    return (
        <Link
            to={item.href}
            target={item.external ? "_blank" : undefined}
            className={cn("text-sm font-medium text-muted-foreground transition-colors hover:text-primary", {
                "text-primary": item.active
            })}>
            {item.label}
        </Link>
    );
};
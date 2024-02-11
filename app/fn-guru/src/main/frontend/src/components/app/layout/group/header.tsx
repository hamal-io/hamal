import React, {FC} from "react";
import {useAuth, useLogout} from "@/hook/auth.ts";
import {Link, useLocation, useParams} from "react-router-dom";
import {cn} from "@/utils";
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Avatar, AvatarFallback} from "@/components/ui/avatar.tsx";

const GroupHeader: FC = () => {
    return (
        <div className="border-b bg-white">
            <div className="flex h-16 px-4">
                <div className="w-full flex items-center justify-between space-x-4">
                    <Nav className="mx-6"/>
                    <Profile/>
                </div>
            </div>
        </div>
    )
}

export default GroupHeader;

const Nav = ({className, ...props}: React.HTMLAttributes<HTMLElement>) => {
    const location = useLocation()
    const currentPath = location.pathname

    const {groupId, namespaceId} = useParams()

    const navigation: NavItem[] = [
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/dashboard`,
            label: "Dashboard",
            active: currentPath === `/groups/${groupId}/namespaces/${namespaceId}/dashboard`
        },
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/playground`,
            label: "Playground",
            active: currentPath === `/groups/${groupId}/namespaces/${namespaceId}/playground`
        },
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/executions`,
            label: "Executions",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/executions`)
        },
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/functions`,
            label: "Functions",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/functions`)
        },
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/schedules`,
            label: "Schedules",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/schedules`)
        },
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/webhooks`,
            label: "Webhooks",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/webhooks`)
        },
        {
            href: `/groups/${groupId}/namespaces/${namespaceId}/endpoints`,
            label: "Endpoints",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/endpoints`)
        },
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

const Profile = () => {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar className="h-8 w-8">
                        <AvatarFallback>You</AvatarFallback>
                    </Avatar>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-32" align="end" forceMount>
                <LogoutMenuItem/>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}


const LogoutMenuItem = () => {
    const [logout,] = useLogout()

    return (
        <DropdownMenuItem onClick={() => {
            logout()
        }}>
            Log out
        </DropdownMenuItem>
    )
}
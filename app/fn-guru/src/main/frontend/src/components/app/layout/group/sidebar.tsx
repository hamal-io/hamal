import {cn} from "@/utils";
import {ActivityIcon, BookOpen, Braces, CalendarIcon, Command, FolderTree, GlobeIcon, LucideIcon, Play, TimerIcon, WebhookIcon} from "lucide-react";
import React, {FC} from "react";
import {Link, useLocation, useParams} from "react-router-dom";
import Profile from "@/components/app/layout/group/profile.tsx";
import GroupNamespaceSelector from "@/components/app/group-namespace-selector.tsx";
import {Separator} from "@/components/ui/separator.tsx";

type Props = {
    className?: string;
};

type NavItem = {
    icon: LucideIcon;
    href: string;
    external?: boolean;
    label: string;
    active?: boolean;
};
const Sidebar: React.FC<Props> = ({className}) => {
    const {groupId, namespaceId} = useParams()

    const location = useLocation()

    const currentPath = location.pathname
    const primaryNavigation: NavItem[] = [
        {
            icon: Command,
            href: `/groups/${groupId}/namespaces/${namespaceId}/dashboard`,
            label: "Dashboard",
            active: currentPath === `/groups/${groupId}/namespaces/${namespaceId}/dashboard`
        },
        {
            icon: Play,
            href: `/groups/${groupId}/namespaces/${namespaceId}/playground`,
            label: "Playground",
            active: currentPath === `/groups/${groupId}/namespaces/${namespaceId}/playground`
        },
        {
            icon: ActivityIcon,
            href: `/groups/${groupId}/namespaces/${namespaceId}/executions`,
            label: "Executions",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/executions`)
        },
        {
            icon: FolderTree,
            href: `/groups/${groupId}/namespaces`,
            label: "Namespaces",
            active: currentPath === `/groups/${groupId}/namespaces`
        },
        {
            icon: Braces,
            href: `/groups/${groupId}/namespaces/${namespaceId}/functions`,
            label: "Functions",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/functions`)
        },
        {
            icon: TimerIcon,
            href: `/groups/${groupId}/namespaces/${namespaceId}/schedules`,
            label: "Schedules",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/schedules`)
        },
        {
            icon: WebhookIcon,
            href: `/groups/${groupId}/namespaces/${namespaceId}/webhooks`,
            label: "Webhooks",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/webhooks`)
        },
        {
            icon: GlobeIcon,
            href: `/groups/${groupId}/namespaces/${namespaceId}/endpoints`,
            label: "Endpoints",
            active: currentPath.startsWith(`/groups/${groupId}/namespaces/${namespaceId}/endpoints`)
        },
    ];


    const secondaryNavigation: NavItem[] = [
        {
            icon: BookOpen,
            href: "https://docs.fn.guru",
            external: true,
            label: "Documentation",
        },
    ];

    return (
        <aside className={cn("fixed h-screen inset-y-0 flex w-48 flex-col px-6 gap-y-5 bg-gray-50", className)}>
            <nav className="flex flex-col ">
                <h1 className="text-2xl font-bold leading-6 text-content mt-6">fn(guru)</h1>
                <div className="pt-8">
                    <GroupNamespaceSelector
                        groupId={groupId}
                        namespaceId={namespaceId}
                    />
                </div>
                <ul className="pt-4">
                    <li>
                        <ul className="mt-2 -mx-2 space-y-1">
                            {primaryNavigation.map((item) => (
                                <li key={item.label}>
                                    <NavLink item={item}/>
                                </li>
                            ))}
                        </ul>
                    </li>
                </ul>
                <div className="fixed bottom-4 left-1 z-50 flex flex-col items-center justify-center">
                    <ul className="pt-4">
                        <li>
                            <ul className="mt-2 -mx-2 space-y-1">
                                {secondaryNavigation.map((item) => (
                                    <li key={item.label}>
                                        <NavLink item={item}/>
                                    </li>
                                ))}
                            </ul>
                        </li>
                    </ul>
                    <Profile/>
                </div>
            </nav>
        </aside>
    );
};

export default Sidebar;

const NavLink: FC<{ item: NavItem }> = ({item}) => {
    return (
        <Link to={item.href}
              target={item.external ? "_blank" : undefined}
              className={cn(
                  "group flex gap-x-2 rounded-md px-2 py-1 text-sm  font-medium leading-6 items-center hover:bg-gray-200",
                  {
                      "bg-gray-200": item.active,
                  },
              )}
        >
      <span
          className="text-content-subtle border-border group-hover:shadow  flex h-6 w-6 shrink-0 items-center justify-center rounded-lg border text-[0.625rem] font-medium bg-white">
        <item.icon className="w-4 h-4 shrink-0" aria-hidden="true"/>
      </span>
            {item.label}
        </Link>
    );
};
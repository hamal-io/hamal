import {cn} from "@/utils";
import {BookOpen, Braces, CalendarIcon, Command, LucideIcon, Play, TimerIcon, WebhookIcon} from "lucide-react";
import React, {FC} from "react";
import {Link, useLocation, useParams} from "react-router-dom";
import FlowSelector from "@/components/app/flow-selector.tsx";

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
    const location = useLocation()
    const {flowId} = useParams()

    const currentPath = location.pathname
    const navigation: NavItem[] = [
        {
            icon: Command,
            href: `/flows/${flowId}`,
            label: "Overview",
            active: currentPath === `/flows/${flowId}`
        },
        {
            icon: Play,
            href: `/flows/${flowId}/executions`,
            label: "Executions",
            active: currentPath.startsWith(`/flows/${flowId}/executions`)
        },
        {
            icon: Braces,
            href: `/flows/${flowId}/functions`,
            label: "Functions",
            active: currentPath.startsWith(`/flows/${flowId}/functions`)
        },
        {
            icon: TimerIcon,
            href: `/flows/${flowId}/schedules`,
            label: "Schedules",
            active: currentPath === `/flows/${flowId}/schedules`
        },
        {
            icon: WebhookIcon,
            href: `/flows/${flowId}/hooks`,
            label: "Webhooks",
            active: currentPath === `/flows/${flowId}/hooks`
        },
        {
            icon: WebhookIcon,
            href: `/flows/${flowId}/endpoints`,
            label: "Endpoints",
            active: currentPath === `/flows/${flowId}/endpoints`
        },
        {
            icon: BookOpen,
            href: "https://docs.fn.guru",
            external: true,
            label: "Documentation",
        },
    ];

    return (
        <aside className={cn("fixed h-screen inset-y-0 flex w-64 flex-col px-6 gap-y-5 bg-gray-100", className)}>
            <nav className="flex flex-col flex-1 flex-grow">
                <FlowSelector flowId={flowId}/>
                <ul className="flex flex-col flex-1 gap-y-7 pt-4">
                    <li>
                        <h3 className="text-xs font-semibold leading-6 text-content">General</h3>
                        <ul className="mt-2 -mx-2 space-y-1">
                            {navigation.map((item) => (
                                <li key={item.label}>
                                    <NavLink item={item}/>
                                </li>
                            ))}
                        </ul>
                    </li>
                </ul>
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

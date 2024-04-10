import {cn} from "@/utils";
import {
    ActivityIcon,
    BookOpen,
    Braces,
    ClipboardPaste,
    Command,
    GlobeIcon,
    Layers3Icon,
    LucideIcon,
    LucideProps,
    Play,
    Settings,
    TimerIcon, User,
    WebhookIcon
} from "lucide-react";
import React, {FC, useEffect, useState} from "react";
import {Link, useLocation} from "react-router-dom";
import {useNamespaceGet} from "@/hook";
import {useUiState} from "@/hook/ui-state.ts";

type Props = { className?: string; };
const Sidebar: React.FC<Props> = ({className}) => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const location = useLocation()
    const currentPath = location.pathname
    const [activeFeatures, setActiveFeatures] = useState(null)


    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, []);

    useEffect(() => {
        if (namespace) {
            const actives = []
            for (const [feat, valid] of Object.entries(namespace.features)) {
                if (valid) {
                    actives.push(featureLinks[feat])
                }
            }
            setActiveFeatures(actives)
        }
    }, [namespace]);



    const primaryNavigation = [
        {
            icon: Command,
            href: `/dashboard`,
            label: "Dashboard",
            active: currentPath === `/dashboard`
        },
        {
            icon: Play,
            href: `/adhoc`,
            label: "Adhoc",
            active: currentPath === `/adhoc`
        },
        {
            icon: ActivityIcon,
            href: `/executions`,
            label: "Executions",
            active: currentPath.startsWith(`/executions`)
        },
        {
            icon: Braces,
            href: `/functions`,
            label: "Functions",
            active: currentPath.startsWith(`/functions`)
        }
    ]


    const featureLinks =
        {
            schedule: {
                icon: TimerIcon,
                href: `/schedules`,
                label: "Schedules",
                active: currentPath.startsWith(`/schedules`)
            },
            topic: {
                icon: Layers3Icon,
                href: `/topics`,
                label: "Topics",
                active: currentPath.startsWith(`/topics`)
            },
            webhook: {
                icon: WebhookIcon,
                href: `/webhooks`,
                label: "Webhooks",
                active: currentPath.startsWith(`/webhooks`)
            },
            endpoint: {
                icon: GlobeIcon,
                href: `/endpoints`,
                label: "Endpoints",
                active: currentPath.startsWith(`/endpoints`)
            }
        }


    const secondaryNavigation = [

        {
            icon: ClipboardPaste,
            href: '/blueprints',
            label: "Blueprints"
        },
        {
            icon: Settings,
            href: "/workspace",
            label: "Settings",
            active: currentPath.startsWith(`/workspace`)
        },
        {
            icon: BookOpen,
            href: "https://docs.fn.guru",
            external: true,
            label: "Documentation",
        },
        {
            icon: User,
            href: "/account",
            label: "Account",
            active: currentPath.startsWith("/account")
        }

    ];


    return (
        <aside className={cn("fixed h-screen inset-y-0 flex w-48 flex-col px-6 gap-y-5 bg-gray-100", className)}>
            <nav className="flex flex-col ">
                <h1 className="text-2xl font-bold leading-6 text-content mt-6">fn(guru)</h1>
                <ol className="pt-4">
                    <li>
                        <ol className="mt-2 -mx-2 space-y-1">
                            {primaryNavigation.map((item) => (
                                <li key={item.label}>
                                    <NavLink item={item}/>
                                </li>
                            ))}
                            {activeFeatures && activeFeatures.map(item =>
                                <li key={item.label}>
                                    <NavLink item={item}/>
                                </li>
                            )}

                        </ol>

                    </li>
                </ol>


                <div className="fixed bottom-4 flex flex-col items-center justify-center">
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
                </div>
            </nav>
        </aside>
    );
};

export default Sidebar;

type NavItem = {
    icon: LucideIcon;
    href: string;
    label: string;
    external?: boolean;
    active?: boolean;
};

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
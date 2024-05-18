import {cn} from "@/utils";
import {FolderTree, HomeIcon, LucideIcon} from "lucide-react";
import React, {FC} from "react";
import {Link, useLocation} from "react-router-dom";

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
const WorkspaceSidebar: React.FC<Props> = ({className}) => {
    const location = useLocation()
    const currentPath = location.pathname
    const primaryNavigation: NavItem[] = [
        {
            icon: HomeIcon,
            href: `/workspace`,
            label: "General",
            active: currentPath === `/workspace`
        },
        {
            icon: FolderTree,
            href: `/workspace/namespaces`,
            label: "Namespaces",
            active: currentPath === `/workspace/namespaces`
        },
    ];

    return (
        <aside className={cn("fixed h-screen inset-y-0 flex w-48 flex-col gap-y-5 bg-gray-50", className)}>
            <nav className="h-screen flex flex-col justify-center items-center">
                <ul>
                    {primaryNavigation.map((item) => (
                        <li key={item.label}>
                            <NavLink item={item}/>
                        </li>
                    ))}
                </ul>
            </nav>
        </aside>
    );
};

export default WorkspaceSidebar;

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
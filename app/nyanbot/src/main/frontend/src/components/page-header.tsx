import React from "react";

type Props = {
    title?: string;
    description?: string;
    actions?: React.ReactNode[];
};

export const PageHeader: React.FC<Props> = ({title, description, actions}) => {
    return (
        <div
            className="flex flex-col items-start justify-between w-full gap-2 mb- md:items-center md:flex-row md:mb-4 md:gap-4">
            <div className="space-y-1 ">
                {title && <h2 className="text-2xl font-semibold tracking-tight">{title}</h2>}
                {description && <p className="text-sm text-gray-500 dark:text-gray-400">{description}</p>}
            </div>
            <ul className="flex items-center justify-end gap-2 md:gap-4 flex-wrap md:flex-nowrap">
                {actions.map((action, index) => (
                    <li key={index}>{action}</li>
                ))}
            </ul>

        </div>
    );
};

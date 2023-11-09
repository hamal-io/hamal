import * as React from "react";

import {cn} from "@/utils";

type EmptyPlaceholderProps = {
    className: string;
    children?: React.ReactNode[];
}

export const EmptyPlaceholder = ({className, children}: EmptyPlaceholderProps) => (
    <div
        className={cn(
            "animate-in relative fade-in-50 flex min-h-[400px] flex-col items-center justify-center rounded-md border-2 border-dashed p-8 text-center",
            className
        )}
    >
        <div className="mx-auto flex max-w-[420px] flex-col items-center justify-center text-center">
            {children}
        </div>
    </div>
);

EmptyPlaceholder.Icon = ({children}: { children: React.ReactNode }) => (
    <div className="flex items-center justify-center w-20 h-20 rounded-full bg-background-subtle">
        {children}
    </div>
);

EmptyPlaceholder.Title = ({className, children}: React.HTMLAttributes<HTMLHeadingElement>) => (
    <h2 className={cn("mt-6 text-xl font-semibold", className)}> {children} </h2>
);

EmptyPlaceholder.Description = ({className, children}: React.HTMLAttributes<HTMLParagraphElement>) => (
    <p
        className={cn(
            "text-content-subtle mb-8 mt-2 text-center text-sm font-normal leading-6",
            className,
        )}
    >{children}</p>
)

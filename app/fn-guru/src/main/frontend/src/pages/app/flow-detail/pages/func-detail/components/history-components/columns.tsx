import {ColumnDef} from "@tanstack/react-table";
import {Deployment} from "@/types";
import {ColumnHeader} from "@/pages/app/flow-detail/pages/func-detail/components/history-components/column-header.tsx";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {DotsHorizontalIcon} from "@radix-ui/react-icons";
import React from "react";
import {CodeCallback} from "@/hook/code.ts";

export const myColumns = (codeCallback: CodeCallback): ColumnDef<Deployment>[] => {
    return [
        {
            accessorKey: "version",
            header: ({column}) => (
                <ColumnHeader column={column} title="Version:"/>
            ),
            cell: ({row}) => <div className="">{row.getValue("version")}</div>,
            enableSorting: false,
            enableHiding: false,
        },
        {
            accessorKey: "message",
            header: ({column}) => (
                <ColumnHeader column={column} title="Message:"/>
            ),
            cell: ({row}) => <div className="">{row.getValue("message")}</div>,
            enableSorting: false,
            enableHiding: false,
        },
        {
            accessorKey: "deployedAt",
            header: ({column}) => (
                <ColumnHeader column={column} title="Time:"/>
            ),
            cell: ({row}) => <div className="">{row.getValue("deployedAt")}</div>,
            enableSorting: false,
            enableHiding: false,
        },

        {
            id: "actions",
            cell: ({row}) =>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            className="flex h-8 w-8 p-0 data-[state=open]:bg-muted float-right"
                        >
                            <DotsHorizontalIcon className="h-4 w-4"/>
                            <span className="sr-only">Open menu</span>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-[160px]">
                        <DropdownMenuItem onClick={() => {
                            console.log(`selected row: ${row.getValue("version")}`)
                            codeCallback(row.getValue("version"))
                        }}>Checkout</DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
        },
    ]
}

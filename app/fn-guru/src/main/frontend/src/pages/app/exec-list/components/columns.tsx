import {ColumnDef} from "@tanstack/react-table"

import {statuses} from "./data.tsx"
import {ColumnHeader} from "./column-header.tsx"
import React from "react";
import RowActions from "@/pages/app/exec-list/components/row-actions.tsx";
import {ExecListItem} from "@/types";

export const columns: ColumnDef<ExecListItem>[] = [
    {
        accessorKey: "id",
        header: ({column}) => (
            <ColumnHeader column={column} title="Id"/>
        ),
        cell: ({row}) => <div className="">{row.getValue("id")}</div>,
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "status",
        header: ({column}) => (
            <ColumnHeader column={column} title="Status"/>
        ),
        cell: ({row}) => {
            const status = statuses.find(
                (status) => status.value === row.getValue("status")
            )

            if (!status) {
                return null
            }

            return (
                <div className="flex  items-center">
                    {status.icon && (
                        <status.icon className="mr-2 h-4 w-4 text-muted-foreground"/>
                    )}
                    <span>{status.label}</span>
                </div>
            )
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id))
        },
    },
    {
        id: "actions",
        cell: ({row}) => <RowActions row={row}/>,
    },
]

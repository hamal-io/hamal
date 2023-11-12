import {ColumnDef} from "@tanstack/react-table"

import {statuses} from "../data/data"
import {ColumnHeader} from "./column-header.tsx"
import {ApiExecSimple} from "@/api/types";
import React from "react";
import RowActions from "@/pages/app/flow-detail/pages/exec-list/components/components/row-actions.tsx";

export const columns: ColumnDef<ApiExecSimple>[] = [
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

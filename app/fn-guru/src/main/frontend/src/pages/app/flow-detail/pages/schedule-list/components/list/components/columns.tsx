import {ColumnDef} from "@tanstack/react-table"

import {types} from "../data/data.tsx"
import {ColumnHeader} from "./column-header.tsx"
import React from "react";
import RowActions from "@/pages/app/flow-detail/pages/schedule-list/components/list/components/row-actions.tsx";
import {TriggerListItem} from "@/hook/api/schedule.tsx";

export const columns: ColumnDef<TriggerListItem>[] = [
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
        accessorKey: "type",
        header: ({column}) => (
            <ColumnHeader column={column} title="Type"/>
        ),
        cell: ({row}) => {
            const type = types.find(
                (type) => type.value === row.getValue("type")
            )

            if (!type) {
                return null
            }

            return (
                <div className="flex  items-center">
                    {type.icon && (
                        <type.icon className="mr-2 h-4 w-4 text-muted-foreground"/>
                    )}
                    <span>{type.label}</span>
                </div>
            )
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id))
        },
    },
    {
        accessorKey: "name",
        header: ({column}) => (
            <ColumnHeader column={column} title="Name"/>
        ),
        cell: ({row}) => {
            return (
                <div className="flex  items-center">
                    <span>{row.getValue("name")}</span>
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

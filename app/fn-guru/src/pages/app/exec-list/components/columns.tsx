import {ColumnDef} from "@tanstack/react-table"

import {invocations, statuses} from "./data.tsx"
import {ColumnHeader} from "./column-header.tsx"
import React from "react";
import RowActions from "@/pages/app/exec-list/components/row-actions.tsx";
import {ExecListItem, ExecListItemFunc, ExecListItemInvocation, ExecListItemNamespace, ExecTriggerItem} from "@/types";

export const columns: ColumnDef<ExecListItem>[] = [
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
        accessorKey: "id",
        header: ({column}) => (
            <ColumnHeader column={column} title="Id"/>
        ),
        cell: ({row}) => <div className="">{row.getValue("id")}</div>,
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "namespace",
        header: ({column}) => (
            <ColumnHeader column={column} title="Namespace"/>
        ),
        cell: ({row}) => {
            const {name} = row.getValue<ExecListItemNamespace>("namespace")

            return (
                <div className="flex  items-center">
                    <span>{name}</span>
                </div>
            )
        },
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "func",
        header: ({column}) => (
            <ColumnHeader column={column} title="Func"/>
        ),
        cell: ({row}) => {
            const {id, name} = (row.getValue<ExecListItemFunc>("func") || {})

            return (
                <div className="flex  items-center">
                    <span>{name}</span>
                </div>
            )
        },
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "correlation",
        header: ({column}) => (
            <ColumnHeader column={column} title="Correlation"/>
        ),
        cell: ({row}) => {
            const correlation = row.getValue<string>("correlation")

            return (
                <div className="flex  items-center">
                    <span>{correlation}</span>
                </div>
            )
        },
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "trigger",
        header: ({column}) => (
            <ColumnHeader column={column} title="Trigger"/>
        ),
        cell: ({row}) => {
            const trigger = row.getValue<ExecTriggerItem>("trigger")

            if (!trigger) {
                return null
            }

            return (
                <div className="flex  items-center">
                    <span>{trigger.type} ({trigger.status})</span>
                </div>
            )

        },
        enableSorting: false,
        enableHiding: false
    },
    {
        id: "actions",
        cell: ({row}) => <RowActions row={row}/>,
    },
]
import {ColumnHeader} from "@/pages/app/func-detail/components/history-components/column-header.tsx";
import RowAction from "@/pages/app/func-detail/components/history-components/row-actions.tsx";
import React from "react";


export const columns = [
    {
        accessorKey: "version",
        header: ({column}) => (
            <ColumnHeader column={column} title="Version:"/>
        ),
        cell: ({row}) => <div className="">{row.getValue("version")}</div>,
        enableSorting: true,
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
        cell: ({row}) => <div className="">{toUTC(row.getValue("deployedAt"))}</div>,
        enableSorting: false,
        enableHiding: false,
    },
    {
        id: "actions",
        cell: ({row}) => <RowAction row={row}/>
    },
]


const toUTC = (epoch: number): string => {
    const date = new Date(epoch)
    return date.toUTCString()
}

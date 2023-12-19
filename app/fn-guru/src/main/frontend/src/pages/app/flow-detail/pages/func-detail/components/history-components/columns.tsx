import {ColumnDef} from "@tanstack/react-table";
import {Deployment} from "@/types";
import {ColumnHeader} from "@/pages/app/flow-detail/pages/func-detail/components/history-components/column-header.tsx";
import RowAction from "@/pages/app/flow-detail/pages/func-detail/components/history-components/row-actions.tsx";
import React from "react";
import {CodeCallback} from "@/types/code.ts";


export const columns = (codeCallback: CodeCallback): ColumnDef<Deployment>[] => {
    return [
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
            cell: ({row}) => <RowAction row={row} codeCallback={codeCallback}/>
        },
    ]
}

const toUTC = (epoch: number): string => {
    const date = new Date(epoch)
    return date.toUTCString()
}

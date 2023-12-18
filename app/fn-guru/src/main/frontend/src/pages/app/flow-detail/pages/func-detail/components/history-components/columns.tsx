import React from "react";
import {ColumnDef} from "@tanstack/react-table";
import {Deployment} from "@/types";
import RowActions from "@/pages/app/flow-detail/pages/func-detail/components/history-components/row-actions.tsx";
import {ColumnHeader} from "@/pages/app/flow-detail/pages/func-detail/components/history-components/column-header.tsx";

export const columns: ColumnDef<Deployment>[] = [
    {
        accessorKey: "version",
        header: ({column}) => (
            <ColumnHeader column={column} title="Version"/>
        ),
        cell: ({row}) => <div className="">{row.getValue("version")}</div>,
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "message",
        header: ({column}) => (
            <ColumnHeader column={column} title="Message"/>
        ),
        cell: ({row}) => <div className="">{row.getValue("message")}</div>,
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: "deployedAt",
        header: ({column}) => (
            <ColumnHeader column={column} title="Time"/>
        ),
        cell: ({row}) => <div className="">{row.getValue("deployedAt")}</div>,
        enableSorting: false,
        enableHiding: false,
    },

    {
        id: "actions",
        cell: ({row}) => <RowActions row={row}/>,
    },
]
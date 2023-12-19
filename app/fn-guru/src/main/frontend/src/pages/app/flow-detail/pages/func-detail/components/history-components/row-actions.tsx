import React from "react";
import {Row} from "@tanstack/react-table";
import {Deployment} from "@/types";
import {useNavigate} from "react-router-dom";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {DotsHorizontalIcon} from "@radix-ui/react-icons";
import {CodeCallback} from "@/hook/code.ts";


interface Props {
    row: Row<Deployment>
    codeCallback: CodeCallback;
}

export default function ({row, codeCallback}: Props) {
    const navigate = useNavigate()

    return (
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
    )
}
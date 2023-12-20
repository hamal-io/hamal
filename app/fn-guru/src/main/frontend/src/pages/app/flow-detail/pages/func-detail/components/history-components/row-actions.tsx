import {Row} from "@tanstack/react-table";
import {Deployment} from "@/types";
import React from "react";
import {DropdownMenu, DropdownMenuContent, DropdownMenuTrigger} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {DotsHorizontalIcon} from "@radix-ui/react-icons";


interface Props {
    row: Row<Deployment>
}

export default function ({row}: Props) {
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
                {/* <DropdownMenuItem onClick={() => {

                }}>Checkout</DropdownMenuItem>*/}
            </DropdownMenuContent>
        </DropdownMenu>
    )
}

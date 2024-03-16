import {BlueprintListItem} from "@/types/blueprint.ts";
import React, {FC, useState} from "react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Dialog} from "@/components/ui/dialog.tsx";
import {BlueprintDialog} from "@/pages/app/blueprint-list/components/dialog.tsx";

type CardProps = {
    blueprint: BlueprintListItem

}
const BlueprintCard: FC<CardProps> = ({blueprint}) => {
    const [open, setOpen] = useState(false)

    return (
        <>
            <Card
                onClick={() => setOpen(true)}
                className="relative overfunc-hidden duration-500 hover:border-primary/50 group pointerCursor"
            >
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle>{blueprint.name}</CardTitle>
                </CardHeader>
                <CardContent>
                    <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                        <div className="flex justify-between py-3 gap-x-4">
                            {blueprint.description}
                        </div>
                    </dl>
                    <div className="flex flex-row justify-between items-center"/>
                </CardContent>
            </Card>
            <Dialog open={open} onOpenChange={setOpen}>
                <BlueprintDialog item={blueprint} onClose={() => setOpen(false)}/>
            </Dialog>
        </>
    )
}

export default BlueprintCard


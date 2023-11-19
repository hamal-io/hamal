import React, {FC, useEffect, useState} from "react";

import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {HookWithTriggers} from "@/pages/app/flow-detail/pages/hook-list/type.tsx";

type Prop = {
    item: HookWithTriggers
}

const Detail: FC<Prop> = ({item}) => {
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    return (
        <>
            <Dialog
                open={openDialog}
                onOpenChange={setOpenDialog}
            >
                <DialogTrigger asChild>

                    <Card
                        key={item.hook.id}
                        className="relative overflow-hidden duration-500 hover:border-primary group"
                    >
                        <CardHeader>
                            <div className="flex items-center justify-between ">
                                <CardTitle>{item.hook.name}</CardTitle>
                            </div>
                        </CardHeader>
                        <CardContent>
                            <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                                <div className="flex justify-between py-3 gap-x-4">
                                    {import.meta.env.VITE_BASE_URL}/v1/webhooks/{item.hook.id}
                                </div>
                            </dl>
                        </CardContent>
                    </Card>

                </DialogTrigger>

                <DialogContent
                    className="fixed left-[50%] top-[50%] z-50 w-full max-w-7xl h-screen"
                >
                    <DialogHeader>
                        {JSON.stringify(item)}
                    </DialogHeader>
                </DialogContent>
            </Dialog>
        </>
    )
}


export default Detail;
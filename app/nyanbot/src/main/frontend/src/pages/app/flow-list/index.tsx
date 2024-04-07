import React, {FC} from "react";
import {CardLarge} from "@/pages/app/flow-list/components/cardLarge.tsx";
import {CardSmall} from "@/pages/app/flow-list/components/CardSmall.tsx";

const FlowListPage = () => {

    return (
        <>
            <div className="flex flex-col mt-12 lg:mt-16 space-y-5 sm:space-y-0 sm:space-x-3 sm:flex-row sm:justify-between sm:items-center">
                <CardLarge></CardLarge>
                <CardLarge></CardLarge>

            </div>
            <div className="grid grid-rows-3 gap-6 xl:gap-8 sm:col-span-6 xl:col-span-2">
                <CardSmall></CardSmall>
                <CardSmall></CardSmall>
            </div>
        </>
    )
}

export default FlowListPage;
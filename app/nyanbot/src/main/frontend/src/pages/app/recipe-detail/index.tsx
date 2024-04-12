import {PageHeader} from "@/components/page-header.tsx";
import React from "react";

const RecipeDetailPage = () => {
    return (
        <main className="flex justify-center w-screen min-h-screen ">
            <div className="rounded-3xl w-11/12 h-5/6 md:w-9/12 md:h-5/6 overflow-y-auto">
                <PageHeader actions={[]}/>
                {/*  {flowList.length !== 0 ?
                    <ol className="flex flex-col gap-4 cursor-pointer">
                        {flowList.map(flow =>
                            <li key={flow.id}>
                                <FlowCard flow={flow}/>
                            </li>
                        )}
                    </ol> : <NoContent/>

                }*/}
            </div>
        </main>
    )
}

export default RecipeDetailPage
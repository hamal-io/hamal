import {PageHeader} from "@/components/page-header.tsx";
import React from "react";
import TagFilter from "@/pages/app/recipe-list/components/tag-filter.tsx";

const RecipeListPage = () => {

    return (
        <main className="flex justify-center w-screen min-h-screen ">
            <div className="rounded-3xl w-11/12 h-5/6 md:w-9/12 md:h-5/6 overflow-y-auto">
                <PageHeader actions={[]}/>
                <section className={"flex flex-col gap-4"}>
                    <div>
                        <TagFilter/>
                    </div>
                    <div>

                    </div>
                </section>
            </div>
        </main>
    )
}

export default RecipeListPage
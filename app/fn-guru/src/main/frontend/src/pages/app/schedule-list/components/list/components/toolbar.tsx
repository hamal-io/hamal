import {Table} from "@tanstack/react-table"
import FacetedFilter from "./faceted-filter.tsx"
import {types} from "@/pages/app/schedule-list/components/list/data/data.tsx";

interface Props<TData> {
    table: Table<TData>
}

export default function <TData>({table}: Props<TData>) {
    return (
        <div className="flex items-center justify-between">
            <div className="flex flex-1 items-center space-x-2">
                {table.getColumn("type") && (
                    <FacetedFilter
                        column={table.getColumn("type")}
                        title="Type"
                        options={types}
                    />
                )}
            </div>
        </div>
    )
}

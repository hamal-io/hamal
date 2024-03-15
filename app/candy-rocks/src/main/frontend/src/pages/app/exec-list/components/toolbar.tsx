import {Table} from "@tanstack/react-table"
import FacetedFilter from "./faceted-filter.tsx"
import {statuses} from "@/pages/app/exec-list/components/data.tsx";

interface Props<TData> {
    table: Table<TData>
}

export default function <TData>({table}: Props<TData>) {
    return (
        <div className="flex items-center justify-between">
            <div className="flex flex-1 items-center space-x-2">
                {table.getColumn("status") && (
                    <FacetedFilter
                        column={table.getColumn("status")}
                        title="Status"
                        options={statuses}
                    />
                )}
            </div>
        </div>
    )
}

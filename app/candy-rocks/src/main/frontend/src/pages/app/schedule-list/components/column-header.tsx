import { Column } from "@tanstack/react-table"

import { cn } from "@/utils"
import { Button } from "@/components/ui/button.tsx"
import React, {HTMLAttributes} from "react";

interface Props<TData, TValue> extends HTMLAttributes<HTMLDivElement> {
  column: Column<TData, TValue>
  title: string
}
export function ColumnHeader<TData, TValue>({column, title, className}: Props<TData, TValue>) {
  if (!column.getCanSort()) {
    return <div className={cn(className)}>{title}</div>
  }

  return (
    <div className={cn("flex items-center space-x-2", className)}>
          <Button
            variant="ghost"
            size="sm"
          >
            <span>{title}</span>
          </Button>
    </div>
  )
}

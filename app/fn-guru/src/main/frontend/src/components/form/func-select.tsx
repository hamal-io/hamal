import {useFuncList} from "@/hook";
import React, {useEffect} from "react";
import {FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {useUiState} from "@/hook/ui-state.ts";

const FormFuncSelect = ({name, form}) => {
    const [uiState] = useUiState()
    const [listFuncs, funcList, loading] = useFuncList()

    useEffect(() => {
        listFuncs(uiState.namespaceId)
    }, [uiState.namespaceId]);

    if (funcList == null || loading || !form) {
        return "Loading..."
    }

    return (

        <FormField
            control={form.control}
            name={name}
            render={({field}) => (
                <FormItem>
                    <FormLabel>Function</FormLabel>
                    <div className="relative w-max">
                        <Select
                            onValueChange={field.onChange}
                        >
                            <FormControl>
                                <SelectTrigger className="w-[280px]">
                                    <SelectValue placeholder="Select a function"/>
                                </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                                <SelectGroup>
                                    {funcList.funcs.map(func =>
                                        <SelectItem key={func.id} value={func.id}> {func.name} </SelectItem>
                                    )}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <FormDescription>
                        The function will be invoked by your trigger
                    </FormDescription>
                    <FormMessage/>
                </FormItem>
            )}
        />
    )
}

export default FormFuncSelect;
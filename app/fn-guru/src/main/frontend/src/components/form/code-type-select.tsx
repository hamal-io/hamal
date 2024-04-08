import React from "react";
import {FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";

const FormCodeTypeSelect = ({name, form}) => {
    return (

        <FormField
            control={form.control}
            name={name}
            render={({field}) => (
                <FormItem>
                    <FormLabel>Code Type</FormLabel>
                    <div className="relative w-max">
                        <Select
                            onValueChange={field.onChange}
                        >
                            <FormControl>
                                <SelectTrigger className="w-[280px]">
                                    <SelectValue placeholder="Select code type"/>
                                </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                                <SelectGroup>
                                    <SelectItem value={'Nodes'}> Nodes </SelectItem>
                                    <SelectItem value={'Lua54'}> Lua 5.4 </SelectItem>
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <FormDescription>
                        The type of code you want to be executed
                    </FormDescription>
                    <FormMessage/>
                </FormItem>
            )}
        />
    )
}

export default FormCodeTypeSelect;
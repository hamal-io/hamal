import React from "react";
import {FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";

const FormTopicTypeSelect = ({name, form}) => {
    return (
        <FormField
            control={form.control}
            name={name}
            render={({field}) => (
                <FormItem>
                    <FormLabel>Topic Type</FormLabel>
                    <div className="relative w-max">
                        <Select
                            onValueChange={field.onChange}
                        >
                            <FormControl>
                                <SelectTrigger className="w-[280px]">
                                    <SelectValue placeholder="Select topic visibility"/>
                                </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                                <SelectGroup>
                                    <SelectItem value={'Namespace'}> Namespace </SelectItem>
                                    <SelectItem value={'Group'}> Group </SelectItem>
                                    <SelectItem value={'Public'}> Public </SelectItem>
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <FormDescription>
                        Visibility of your topic
                    </FormDescription>
                    <FormMessage/>
                </FormItem>
            )}
        />
    )
}

export default FormTopicTypeSelect;
import React from "react";
import {FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";

const FormHttpMethodSelect = ({name, form}) => {
    return (

        <FormField
            control={form.control}
            name={name}
            render={({field}) => (
                <FormItem>
                    <FormLabel>Http Method</FormLabel>
                    <div className="relative w-max">
                        <Select
                            onValueChange={field.onChange}
                        >
                            <FormControl>
                                <SelectTrigger className="w-[280px]">
                                    <SelectValue placeholder="Select a http method"/>
                                </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                                <SelectGroup>
                                    <SelectItem value={'Get'}> Get </SelectItem>
                                    <SelectItem value={'Post'}> Post </SelectItem>
                                    <SelectItem value={'Patch'}> Patch </SelectItem>
                                    <SelectItem value={'Put'}> Put </SelectItem>
                                    <SelectItem value={'Delete'}> Delete </SelectItem>
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <FormDescription>
                        The http method which will trigger the function
                    </FormDescription>
                    <FormMessage/>
                </FormItem>
            )}
        />
    )
}

export default FormHttpMethodSelect;
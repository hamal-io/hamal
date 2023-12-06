import * as React from "react";

import {FC, useState} from "react"
import {Button} from "@/components/ui/button.tsx";
import * as z from "zod"
import {useFuncDeployLatestCode, useFuncInvoke} from "@/hook/func.ts";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage
} from "@/components/ui/form.tsx";
import {Popover, PopoverContent, PopoverTrigger, PopoverClose} from "@/components/ui/popover.tsx";
import {Input} from "@/components/ui/input.tsx";


const formSchema = z.object({
    message: z.string().max(50),
})

type Props = {
    funcId: string;
}

const Deploy: FC<Props> = ({funcId}) => {
    const [deployFunc] = useFuncDeployLatestCode()
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            message: "Your message could be here."
        },
    })

    async function handleDeploy(values: z.infer<typeof formSchema>) {
        try {
            deployFunc(funcId, values.message)
        } catch (e) {
            console.error(e)
        }
    }


    return (
        <Popover>
            <PopoverTrigger asChild>
                <Button>
                    Deploy
                </Button>
            </PopoverTrigger>
            <PopoverContent>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(handleDeploy)} className="space-y-8">
                        <FormField
                            control={form.control}
                            name="message"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Leave a deploy message</FormLabel>
                                    <FormControl>
                                        <Input {...field} />
                                    </FormControl>
                                    <FormDescription>
                                        This may take a second.
                                    </FormDescription>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />

                        <Button type="submit" variant="destructive">
                            Confirm
                        </Button>
                    </form>
                </Form>

                <PopoverClose/>
            </PopoverContent>
        </Popover>
    )
}

export default Deploy;


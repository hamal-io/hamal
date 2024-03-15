import React, {FC} from "react";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem} from "@/components/ui/form.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Input} from "@/components/ui/input.tsx";


type Props = { name: string, onChange: (s: string) => void }
export const RenameForm: FC<Props> = ({name, onChange}) => {
    const formSchema = z.object(
        {
            name: z.string().min(2).max(50)
        }
    )

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: name
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        onChange(values.name)
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
                <PageHeader
                    title="Current Namesapce"
                    description="Rename."
                    actions={[
                        <Button type={"submit"} variant={"default"}>
                            Rename
                        </Button>
                    ]}
                />
                <FormField
                    control={form.control}
                    name="name"
                    render={({field}) => (
                        <FormItem>
                            <FormControl>
                                <Input {...field}/>
                            </FormControl>
                        </FormItem>
                    )}
                />
            </form>
        </Form>
    )
}
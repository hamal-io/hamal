import React, {FC, useState} from "react";
import {Form, FormControl, FormField, FormItem, FormLabel} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import Editor from "@/components/editor.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Loader2} from "lucide-react";
import {useForm} from "react-hook-form";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {useBlueprintUpdate} from "@/hook/blueprint.ts";


const formSchema = z.object(
    {
        name: z.string().min(2),
        code: z.string(),
        description: z.string()
    }
)

type Props = { blueprintId: string, name: string, description: string, value: string }
const BlueprintEditorForm: FC<Props> = ({blueprintId, name, description, value}) => {
    const [isLoading, setLoading] = useState(false)
    const [updateBlueprint] = useBlueprintUpdate()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: name,
            description: description,
            code: value
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            updateBlueprint(blueprintId, values.name, values.code, values.description)
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
        }
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
                <FormField
                    control={form.control}
                    name="name"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Name</FormLabel>
                            <FormControl>
                                <Input {...field} />
                            </FormControl>
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="description"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Description</FormLabel>
                            <FormControl>
                                <Input {...field} />
                            </FormControl>
                        </FormItem>
                    )}

                />
                <FormField
                    control={form.control}
                    name="code"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Description</FormLabel>
                            <FormControl>
                                <div className="bg-white p-4 rounded-sm border-2">
                                    <Editor
                                        code={field.value}
                                        onChange={field.onChange}
                                    />
                                </div>
                            </FormControl>
                        </FormItem>
                    )}

                />
                <br/>
                <Button type={"submit"}>
                    {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                    Save
                </Button>
            </form>
        </Form>
    )
}

export default BlueprintEditorForm
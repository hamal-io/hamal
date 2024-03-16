import React, {FC} from "react";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem} from "@/components/ui/form.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Input} from "@/components/ui/input.tsx";
import {useNamespaceUpdate} from "@/hook";
import {Namespace} from "@/types/namespace";
import {Loader2} from "lucide-react";

const formSchema = z.object(
    {
        name: z.string().min(2).max(50)
    }
)

type Props = { namespace: Namespace }
export const RenameForm: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateResponse,loading, error] = useNamespaceUpdate()


    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: namespace.name
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        const abortController = new AbortController()
        updateNamespace(namespace.id, values.name, namespace.features, abortController)
        return (() => abortController.abort())
    }

    return (
        <Form {...form} >
            <form onSubmit={form.handleSubmit(onSubmit)} >
                <FormField
                    control={form.control}
                    name="name"
                    render={({field}) => (
                        <FormItem >
                            <div className={"flex flex-row gap-4"}>
                            <FormControl>
                                <Input {...field}/>
                            </FormControl>
                            <Button type={"submit"} variant={"default"}>
                                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Rename
                            </Button>
                            </div>
                        </FormItem>

                    )}
                />
            </form>
        </Form>
    )
}
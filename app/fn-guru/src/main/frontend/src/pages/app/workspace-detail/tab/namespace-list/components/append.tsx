import React, {FC, useEffect, useState} from "react";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {Globe, Layers3, Loader2, Timer, Webhook} from "lucide-react";
import {DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useNamespaceAppend} from "@/hook";
import {NamespaceFeatures} from "@/types";
import {FeatureCard} from "@/pages/app/dashboard/components/feature.tsx";


const formSchema = z.object({
    name: z.string().min(2).max(50),
    features: z.object({
        schedule: z.boolean(),
        topic: z.boolean(),
        webhook: z.boolean(),
        endpoint: z.boolean()
    })
})


type Props = { appendTo: string, onClose: () => void }
const Append: FC<Props> = ({appendTo, onClose}) => {
    const [isLoading, setLoading] = useState(false)
    const [appendNamespace] = useNamespaceAppend()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            features: {
                schedule: true,
                topic: false,
                webhook: false,
                endpoint: false
            }
        }
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            appendNamespace(appendTo, values.name, {...values.features} as NamespaceFeatures)
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
            onClose()
        }
    }

    return (
        <DialogContent>
            <DialogHeader>Create Namespace</DialogHeader>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                    <FormField
                        control={form.control}
                        name="name"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Name</FormLabel>
                                <FormControl>
                                    <Input  {...field} />
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="features"
                        render={({field}) => (
                            <FormItem>
                                <FormControl>
                                    <FeatureSelect
                                        defaults={field.value as NamespaceFeatures}
                                        onSave={(feat) => {
                                            field.onChange(feat)
                                        }}
                                    />
                                </FormControl>
                            </FormItem>
                        )
                        }
                    />
                    <Button type="submit">
                        {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                        Create
                    </Button>
                </form>
            </Form>
        </DialogContent>
    )
}

export default Append;
type FeatureSelectProps = {
    defaults: NamespaceFeatures
    onSave: (val: NamespaceFeatures) => void
}
const FeatureSelect: FC<FeatureSelectProps> = ({defaults, onSave}) => {
    const [features, setFeatures] = useState<NamespaceFeatures>(defaults)
    const [isUpdate, setIsUpdate] = useState(null)

    useEffect(() => {
        if (isUpdate === true) {
            onSave(features)
            setIsUpdate(false)
        }
    }, [features, isUpdate]);

    function handleChange(name: string) {
        setFeatures(prevState => ({...prevState, [name]: !prevState[name]}));
        setIsUpdate(true)
    }

    const items = {
        schedule: {
            name: "schedule",
            label: "Schedule",
            icon: Timer,
            description: "All kinds of timers",
            checked: features.schedule,
            onChange: handleChange
        },
        topic: {
            name: "topic",
            label: "Topic",
            icon: Layers3,
            description: "Stay tuned",
            checked: features.topic,
            onChange: handleChange
        },
        webhook: {
            name: "webhook",
            label: "Webhook",
            icon: Webhook,
            description: "Stay tuned",
            checked: features.webhook,
            onChange: handleChange
        },
        endpoint: {
            name: "endpoint",
            label: "Endpoint",
            icon: Globe,
            description: "API yourself",
            checked: features.endpoint,
            onChange: handleChange
        }
    }

    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard feature={items.schedule}/>
                <FeatureCard feature={items.topic}/>
                <FeatureCard feature={items.webhook}/>
                <FeatureCard feature={items.endpoint}/>
            </div>
        </div>
    )
}
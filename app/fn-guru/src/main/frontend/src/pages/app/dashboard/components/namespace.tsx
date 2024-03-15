import React, {cloneElement, FC, useEffect, useState} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceGet, useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {FeatureObject} from "@/types";
import {Switch} from "@/components/ui/switch.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Form, FormControl, FormField, FormItem} from "@/components/ui/form.tsx";
import form from "@/pages/app/blueprint-list/components/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import Element = React.JSX.Element;


const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const [updateNamespace, updateRequested, loading2, error2] = useNamespaceUpdate()
    const [featureList, setFeatureList] = useState(new Array<Feature>(
        new Feature("Schedule", 0),
        new Feature("Topic", 1),
        new Feature("Webhook", 2),
        new Feature("Endpoint", 3),
    ))
    const [schedule, topic, webhook, endpoint] = featureList

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    useEffect(() => {
        if (namespace) {
            const client = [...featureList]
            const server = deserializeFeature(namespace.features)
            for (const fs of server) {
                for (const fc of client) {
                    if (fs.value === fc.value) {
                        fc.state = true
                    }
                }
            }
            setFeatureList(client)
        }
    }, [namespace]);



    function updateFeatures(name?: string) {
        const rename = name === null ? namespace.name : name
        try {
            const answer: FeatureObject = {}
            featureList.forEach((f) => {
                if (f.state === true) {
                    answer[f.name] = f.value
                }
            })
            updateNamespace(uiState.namespaceId, rename, answer)

        } catch (e) {
            console.log(e)
        }
    }

    function toggle(value: number) {
        const client = [...featureList]
        const x = client.find((f) => f.value === value)
        x.toggle()
        setFeatureList(client)
    }



    if (error) return "Error"
    if (loading) return "Loading..."
    return (
        <div className="pt-8 px-8">
            {namespace &&
                <RenameForm
                    name={namespace.name}
                    onChange={(s) => updateFeatures(s)}
                />
            }
            <br/>
            <PageHeader
                title="Workloads"
                description="Select workflows for this namespace."
                actions={[
                    <Button variant={"default"} onClick={() => updateFeatures()}>Apply</Button>
                ]}
            />
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedules"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={schedule.state}
                    onCheck={() => toggle(schedule.value)}
                />
                <FeatureCard
                    label={"Topics"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={topic.state}
                    onCheck={() => toggle(topic.value)}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={webhook.state}
                    onCheck={() => toggle(webhook.value)}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={endpoint.state}
                    onCheck={() => toggle(endpoint.value)}
                />
            </div>
        </div>
    )
}


type RenameProps = {
    name: string
    onChange: (s: string) => void
}
const RenameForm: FC<RenameProps> = ({name, onChange}) => {
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
                    )}>
                </FormField>
            </form>
        </Form>
    )
}

type FeatureProps = {
    label: string,
    description: string,
    icon: Element,
    onCheck: () => void
    checked: boolean
}

const FeatureCard: FC<FeatureProps> = ({label, description, onCheck, icon, checked}) => {
    const _icon = cloneElement(icon, {
        size: 32
    })

    return (
        <Card>
            <CardHeader className={"flex flex-row justify-between"}>
                {_icon}
                {label}
                <Switch checked={checked} onCheckedChange={() => onCheck()}></Switch>
            </CardHeader>
            <CardContent>
                <CardDescription>
                    {description}
                </CardDescription>
            </CardContent>
        </Card>
    )
}

export default NamespaceDetailPage


class Feature {
    name: string
    value: number
    state: boolean

    constructor(name: string, value: number) {
        this.name = name;
        this.value = value;
        this.state = false
    }

    toggle() {
        this.state = !this.state
    }
}

function deserializeFeature(obj: FeatureObject): Array<Feature> {
    const res = new Array<Feature>()
    for (const [k, v] of Object.entries(obj)) {
        const f = new Feature(k, v)
        res.push(f)
    }
    return res
}

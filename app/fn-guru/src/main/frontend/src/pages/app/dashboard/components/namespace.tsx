import React from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {NamespaceFeatures} from "@/types";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceUpdate} from "@/hook";

const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [updateNamespace, updateRequested, loading, error] = useNamespaceUpdate()

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Workloads"
                description="Select workflows for this namespace."
                actions={[]}
            />
            <ol className={"grid gap-4 grid-cols-2"}>
                {
                    NamespaceFeatures.map(feature =>
                        <li key={feature.value}>
                            <Card>
                                <CardHeader className={"flex flex-row justify-between"}>
                                    {feature.icon}
                                    {feature.label}
                                    <Checkbox></Checkbox>
                                </CardHeader>
                                <CardContent>
                                    <CardDescription>
                                        {feature.description}
                                    </CardDescription>
                                </CardContent>

                            </Card>
                        </li>
                    )
                }
            </ol>
        </div>
    )
}


export default NamespaceDetailPage
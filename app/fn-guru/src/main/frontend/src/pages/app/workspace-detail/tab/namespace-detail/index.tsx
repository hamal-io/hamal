import {useNavigate, useParams} from "react-router-dom";
import {PageHeader} from "@/components/page-header.tsx";
import React, {FC} from "react";
import {NamespaceFeature, NamespaceFeatures} from "@/pages/app/workspace-detail/tab/namespace-detail/types.ts";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";

const NamespaceDetailPage = () => {
    const {namespaceId} = useParams()
    const navigate = useNavigate()

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                actions={[]}
                description="Add some features"
            />

            <ol className={"grid gap-4 grid-cols-2"}>
                {
                    NamespaceFeatures.map(feature =>
                        <li key={feature.value}>
                            <FeatureCard feature={feature}/>
                        </li>
                    )
                }
            </ol>


        </div>
    )

}

type Props = { feature: NamespaceFeature }
const FeatureCard: FC<Props> = ({feature}) => {
    return (
        <Card className={"flex flex-row  justify-between"}>
            {feature.icon}
            <CardContent className={"flex flex-col"}>
                <CardHeader>
                    {feature.label}
                </CardHeader>
                <CardDescription>
                    {feature.description}
                </CardDescription>
            </CardContent>
            <Checkbox></Checkbox>


        </Card>

    )
}


export default NamespaceDetailPage
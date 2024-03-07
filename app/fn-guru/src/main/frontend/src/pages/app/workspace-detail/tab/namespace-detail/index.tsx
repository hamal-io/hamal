import {useNavigate, useParams} from "react-router-dom";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {ListRestart} from "lucide-react";
import NamespaceNodeEntry from "@/pages/app/workspace-detail/tab/namespace-list/components/node.tsx";
import React from "react";
import {NamespaceFeatures} from "@/pages/app/workspace-detail/tab/namespace-detail/types.ts";

const NamespaceDetailPage = () => {
    const {namespaceId} = useParams()
    const navigate = useNavigate()

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                actions={[

                ]}
                description="Add some features"
            />





        </div>
    )

}


function FeatureCard() {

    const features = NamespaceFeatures.

    return (
        <ol>

        </ol>
    )
}

export default NamespaceDetailPage
import React, {FC} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Namespace} from "@/types";


type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateResponse, loading, error] = useNamespaceUpdate()


    if (error) return "Error"

    return (
        <div className="pt-8 px-8">
           {/* <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedule"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={schedule}
                    onCheck={() => toggle("Schedule")}
                />
                <FeatureCard
                    label={"Topic"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={topic}
                    onCheck={() => toggle("Topic")}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={webhook}
                    onCheck={() => toggle("Webhook")}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={endpoint}
                    onCheck={() => toggle("Endpoint")}
                />
            </div>*/}
        </div>
    )
}
export default FeatureTab



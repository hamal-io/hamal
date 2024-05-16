import {useParams} from "react-router-dom";
import {useEffect} from "react";
import {useFlowGet} from "@/hook/flow.ts";
import {PageHeader} from "@/components/page-header.tsx";

const FlowDetailPage = () => {
    const {flowId} = useParams()
    const [getFlow, flow, loading, error] = useFlowGet()

    useEffect(() => {
        const abortController = new AbortController();
        getFlow(flowId, abortController)
        return (() => abortController.abort())
    }, []);

    if (!flow || loading) return "Loading.."
    if (error) return "Error"

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title={flow.name}
                description={""}
                actions={[]}
            />
            <pre>{JSON.stringify(flow, null, 2)}</pre>


        </div>
    )
}

export default FlowDetailPage
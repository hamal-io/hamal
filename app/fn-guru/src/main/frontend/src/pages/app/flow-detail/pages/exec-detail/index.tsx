import {FC} from "react";
import {useParams} from "react-router-dom";
import Log from "@/pages/app/flow-detail/pages/exec-detail/components/log.tsx";

type Props = {}
const ExecDetailPage: FC<Props> = () => {
    const {execId} = useParams()

    return (
        <div className="w-full">
            <Log execId={execId}/>
        </div>
    )
}

export default ExecDetailPage
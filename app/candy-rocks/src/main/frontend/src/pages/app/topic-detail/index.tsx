import {FC} from "react";
import {useParams} from "react-router-dom";
import Log from "@/pages/app/exec-detail/components/log.tsx";

type Props = {}
const TopicDetailPage: FC<Props> = () => {
    const {topicId} = useParams()

    return (
        <div className="w-full">
            {topicId}
        </div>
    )
}

export default TopicDetailPage
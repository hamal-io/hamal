import {FC} from "react";
import {useParams} from "react-router-dom";

type Props = {}
const TopicDetailPage: FC<Props> = ({}) => {
    const {topicId} = useParams()

    return (
        <div className="w-full">
            {topicId}
        </div>
    )
}

export default TopicDetailPage
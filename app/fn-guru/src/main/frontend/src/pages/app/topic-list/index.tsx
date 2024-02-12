import React, {FC, useContext, useEffect} from "react";
import {GroupLayoutContext} from "@/components/app/layout";
import {PageHeader} from "@/components/page-header.tsx";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useTopicGroupList} from "@/hook/topic.ts";
import {TopicListItem} from "@/types/topic.ts";
import Create from "@/pages/app/topic-list/components/create.tsx";

type Props = {}
const TopicListPage: FC<Props> = ({}) => {
    const {groupId} = useContext(GroupLayoutContext)
    const [listTopics, topicList, loading, error] = useTopicGroupList()

    useEffect(() => {
        const abortController = new AbortController();
        listTopics(groupId, abortController)
        return () => {
            abortController.abort();
        };
    }, [groupId]);

    if (error) return `Error`
    if (topicList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Topics"
                description={`Topics TBD`}
                actions={[<Create/>]}
            />
            {
                topicList.topics.length ? (<Content
                    topics={topicList.topics}
                />) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    topics: TopicListItem[]
}

const Content: FC<ContentProps> = ({topics}) => {
    const {groupId, namespaceId} = useContext(GroupLayoutContext)

    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {topics.map((topic) => (
                <Card
                    key={topic.id}
                    className="relative overtopic-hidden duration-500 hover:border-primary/50 group"
                    onClick={() => {
                        navigate(`/groups/${groupId}/namespaces/${namespaceId}/topics/${topic.id}`)
                    }}
                >
                    <CardHeader>
                        <div className="flex items-center justify-between ">
                            <CardTitle>{topic.name}</CardTitle>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                            <div className="flex justify-between py-3 gap-x-4">
                                Visibility: {topic.type}
                            </div>
                        </dl>
                    </CardContent>
                </Card>
            ))}
        </ul>
    )
}


const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Topics found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Topics yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/topics"}/>
        </div>
    </EmptyPlaceholder>
)

export default TopicListPage
import React, {useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import Create from "@/pages/app/topic-list/components/create.tsx";
import {useUiState} from "@/hook/ui-state.ts";
// import {useTopicsWithFuncs} from "@/pages/app/topic-list/components/hook.ts";
import TopicCard from "@/pages/app/topic-list/components/card.tsx";
import {useTopicList} from "@/hook/topic.ts";


const TopicListPage = () => {
    const [uiState] = useUiState()
    const [update, setUpdate] = useState(true)
    // const [getTopicsWithFuncs, topicsWithFuncs, loading, error] = useTopicsWithFuncs()
    const [listTopics, topicList, loading, error] = useTopicList()

    useEffect(() => {
        if (update) {
            listTopics({
                namespaceId: uiState.namespaceId
            })
            setUpdate(false)
        }
    }, [update]);

    if (topicList == null || loading) return "Loading..."
    if (error) return "Error"

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Topics"
                description={`Topics TBD`}
                actions={[
                    <Create onClose={() => setUpdate(true)}/>
                ]}
            />
            {topicList.topics.length !== 0 ?
                <ul className="grid grid-cols-3 gap-4">
                    {topicList.topics.map(topic =>
                        <li key={topic.id}>
                            <TopicCard
                                namespaceId={uiState.namespaceId}
                                topic={topic}
                            />
                        </li>
                    )}
                </ul> : <EmptyPlaceholder className="my-4 ">
                    <EmptyPlaceholder.Icon>
                        {/*<Code />*/}
                    </EmptyPlaceholder.Icon>
                    <EmptyPlaceholder.Title>No Topics found</EmptyPlaceholder.Title>
                    <EmptyPlaceholder.Description>
                        You haven&apos;t created any Topics yet.
                    </EmptyPlaceholder.Description>
                    <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
                        <Create onClose={() => setUpdate(true)}/>
                        <GoToDocumentation link={"/topics"}/>
                    </div>
                </EmptyPlaceholder>
            }
        </div>
    )
}


export default TopicListPage
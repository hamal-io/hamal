import {FeedbackCreateRequested} from "@/types/feedback.ts";
import {usePost} from "@/hook/http.ts";
import {useCallback} from "react";

type FeedbackCreateAction = (mood: number, message: string, accountId: string, abortController?: AbortController) => void
export const useFeedbackCreate = (): [FeedbackCreateAction, FeedbackCreateRequested, boolean, Error] => {
    const [post, submission, loading, error] = usePost<FeedbackCreateRequested>()
    const fn = useCallback(async (mood: number, message: string, accountId: string, abortController?: AbortController) =>
        post('/v1/feedback',
            {
                mood,
                message,
                accountId
            },
            abortController), []
    )

    return [fn, submission, loading, error]
}
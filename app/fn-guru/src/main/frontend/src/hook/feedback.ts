import {FeedbackCreateSubmitted, FeedbackMood} from "@/types/feedback.ts";
import {usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {useAuth} from "@/hook/auth.ts";

type FeedbackCreateAction = (message: string, mood: FeedbackMood, anon: boolean, abortController?: AbortController) => void
export const useFeedbackCreate = (): [FeedbackCreateAction, FeedbackCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<FeedbackCreateSubmitted>()
    const fn = useCallback(async (message: string, mood: FeedbackMood, anon: boolean , abortController?: AbortController) =>
        post('/v1/feedback/create',
            {
                message,
                mood,
                //anon ? auth.accountId : null
            },
            abortController), [auth]
    )

    return [fn, submission, loading, error]
}
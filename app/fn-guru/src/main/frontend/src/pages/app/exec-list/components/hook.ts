import {ExecList} from "@/types";
import {useAuth} from "@/hook/auth.ts";
import {useExecList, useTriggerList} from "@/hook";
import {useCallback, useEffect, useState} from "react";


type ExecsWithTriggersAction = (namespaceId: string, abortController?: AbortController) => void
export const useExecsWithTriggers = (): [ExecsWithTriggersAction, ExecList, boolean, Error] => {
    const [auth] = useAuth()
    const [listTriggers, triggerList, , triggerError] = useTriggerList()
    const [listExecs, execList, , execError] = useExecList()
    const [loading, setLoading] = useState(true)
    const [result, setResult] = useState<ExecList>(null)

    const fn = useCallback<ExecsWithTriggersAction>(
        async (namespaceId, abortController ?) => {
            listTriggers(namespaceId, abortController)
            listExecs(namespaceId, abortController)
        }, [auth]
    )

    useEffect(() => {
        if (triggerList && execList) {
            const copy = {...execList};

            copy.execs = copy.execs.map(exec => {
                if ((exec.invocation.class === "Schedule" || "Event" || "Hook" || "Endpoint") && exec.func != null) {
                    const execTrigger = triggerList.triggers.find(trigger => trigger.func.id === exec.func.id);
                    return {
                        trigger: {
                            id: execTrigger.id,
                            status: execTrigger.status
                        }, ...exec
                    }
                }
                return exec;
            });

            setResult(copy);
        }
        setLoading(false)
    }, [triggerList, execList]);


    return [fn, result, loading, triggerError || execError]
}
import {useAuth} from "@/hook/auth.ts";
import {useEffect, useState} from "react";

export interface TriggerListItem {
    id: string;
    type: string;
    name: string;
}

export const useListScheduleTriggers = (flowId: string): [TriggerListItem[], boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [triggers, setTriggers] = useState<TriggerListItem[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        const abortController = new AbortController();

        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setIsLoading(false)
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/flows/${flowId}/triggers?types=FixedRate`, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                method: "GET",
                signal: abortController.signal
            })
                .then(response => {

                    console.log("response", JSON.stringify(response))
                    if (response.status === 403) {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }

                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setIsLoading(false)
                    }


                    response.json().then(data => {
                        setTriggers(data.triggers)
                        setIsLoading(false)
                    })
                })
                .catch(error => {
                    if (error.name !== 'AbortError') {
                        // FIXME NETWORK ERROR
                        setError(error)
                        setIsLoading(false)
                    }


                    if (error.message === 'NetworkError when attempting to fetch resource.') {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }
                })
        }
        return () => {
            abortController.abort();
        };
    }, []);

    return [triggers, isLoading, error]
}

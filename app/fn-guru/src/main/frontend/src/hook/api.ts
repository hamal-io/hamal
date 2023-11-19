import {useCallback, useState} from "react";
import {useAuth} from "@/hook/auth.ts";

type ApiPostAction = (url: string, data: object, abortController?: AbortController) => void
export const useApiPost = <T>(): [ApiPostAction, T, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (url: string, body: object, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
            window.location.href = '/'

        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/${url}`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify(body),
                signal: abortController?.signal
            })
                .then(response => {

                    if (response.status === 403) {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/'
                    }

                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setIsLoading(false)
                    }
                    response.json().then(data => {
                        setData(data)
                        setIsLoading(false)
                    })
                })
                .catch(error => {
                    setError(error)
                    setIsLoading(false)
                })
        }
    }, [auth])

    return [post, data, isLoading, error]
}

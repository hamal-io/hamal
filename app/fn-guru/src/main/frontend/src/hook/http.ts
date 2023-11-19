import {useAuth} from "@/hook/auth.ts";
import {useCallback, useState} from "react";
import {Auth} from "@/types/auth.ts";

type GetAction = (url: string) => void
export const useGetAction = <T>(): [GetAction, T, boolean, Error] => {
    const [auth, setAuth] = useAuth()
    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const fn = useCallback(async (url: string) => {
        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
            window.location.href = '/'
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/${url}`, {
                method: "GET",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                }
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

    return [fn, data, isLoading, error]
}

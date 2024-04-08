import React from "react";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {useAuth} from "@/hooks/auth.ts";

export const TestPage = () => {
    const [auth] = useAuth()
    return (
        <ButtonPrimary onClick={() => {
            console.log("Test")

            fetch(`${import.meta.env.VITE_BASE_URL}/v1/adhoc`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({
                    inputs: {},
                    code: "{'hello':'world'}"
                })
            })
                .then(response => {

                })
                .catch(error => {
                    console.error(error)
                })

        }}>
            Test
        </ButtonPrimary>
    )
}
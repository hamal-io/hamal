import React, {FC, useEffect, useState} from 'react'
import {useApiCreateAnonymousAccount, useApiPost} from "@/hook";
import {useLocation, useNavigate} from "react-router-dom";
import {useAuth} from "@/hook/auth.ts";
import {Loader2} from "lucide-react";


const OnboardingPage: FC = () => {
    const location = useLocation()
    const navigate = useNavigate()
    const [auth] = useAuth()
    const [createAnonymousAccount] = useApiCreateAnonymousAccount()
    const [submitFlow, flowsubmitted,] = useApiPost<{ flowId: string }>()
    const [submitBlueprint, blueprintSubmitted] = useApiPost()
    const [code, setCode] = useState<string>('')

    useEffect(() => {
        if (location.state == null) {
            navigate('/', {replace: true})
        } else {
            setCode(location.state.code)
        }
    }, [location.state, navigate]);


    useEffect(() => {
        const abortController = new AbortController()
        if (auth == null || auth.type === 'Unauthorized') {
            createAnonymousAccount(abortController)
        }
        return () => {
            abortController.abort()
        }
    }, []);

    useEffect(() => {
        const abortController = new AbortController()

        if (auth != null && auth.type !== 'Unauthorized') {
            submitFlow(`v1/groups/${auth.groupId}/flows`, {
                name: `Test-Name-Space-${generateId(10)}`,
                inputs: {}
            }, abortController)
        }
        return () => {
            abortController.abort()
        }
    }, [auth, submitFlow])

    useEffect(() => {
        const abortController = new AbortController()

        if (flowsubmitted != null) {
            submitBlueprint(`v1/flows/${flowsubmitted.flowId}/adhoc`, {
                inputs: {},
                code: `sys = require('sys')
                sys.func.create({
                    name = 'Hello-World',
                    inputs = {},
                    code = [[ ${code} ]]
                })`
            }, abortController)
        }
        return () => {
            abortController.abort()
        }

    }, [flowsubmitted, submitBlueprint]);


    useEffect(() => {
        if (blueprintSubmitted != null) {
            navigate('/flows', {replace: true})
        }
    }, [blueprintSubmitted, navigate]);

    return (
        <main className="flex-1 w-full pt-2 mx-auto text-lg h-screen shadow-lg bg-gray-200">
            <section className="container p-4 mx-auto max-w-3xl">
                <div className={"flex flex-row"}>
                    <h1>Your personal flow is deployed shortly.</h1>
                    <div className={"pl-3"}>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin"/>
                    </div>
                </div>
            </section>
        </main>
    );
}

// dec2hex :: Integer -> String
// i.e. 0-255 -> '00'-'ff'
function dec2hex(dec) {
    return dec.toString(16).padStart(2, "0")
}

// generateId :: Integer -> String
function generateId(len) {
    const arr = new Uint8Array((len || 40) / 2)
    window.crypto.getRandomValues(arr)
    return Array.from(arr, dec2hex).join('')
}

export default OnboardingPage


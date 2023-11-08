import React, {FC, useEffect, useState} from 'react'
import {Spinner} from "flowbite-react";
import {useApiCreateAnonymousAccount, useApiPost} from "@/hook";
import {useLocation, useNavigate} from "react-router-dom";
import {useAuth} from "@/hook/auth.ts";


const OnboardingPage: FC = () => {
    const location = useLocation()
    const navigate = useNavigate()
    const [auth] = useAuth()
    const [createAnonymousAccount] = useApiCreateAnonymousAccount()
    const [submitNamespace, namespaceSubmitted,] = useApiPost<{ namespaceId: string }>()
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
            submitNamespace(`v1/groups/${auth.groupId}/namespaces`, {
                name: `Test-Name-Space-${generateId(10)}`,
                inputs: {}
            }, abortController)
        }
        return () => {
            abortController.abort()
        }
    }, [auth, submitNamespace])

    useEffect(() => {
        const abortController = new AbortController()

        if (namespaceSubmitted != null) {
            submitBlueprint(`v1/namespaces/${namespaceSubmitted.namespaceId}/adhoc`, {
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

    }, [namespaceSubmitted, submitBlueprint]);


    useEffect(() => {
        if (blueprintSubmitted != null) {
            navigate('/namespaces', {replace: true})
        }
    }, [blueprintSubmitted, navigate]);

    return (
        <main className="flex-1 w-full pt-2 mx-auto text-lg h-screen shadow-lg bg-gray-200">
            <section className="container p-4 mx-auto max-w-3xl">
                <div className={"flex flex-row"}>
                    <h1>Your personal namespace is deployed shortly.</h1>
                    <div className={"pl-3"}>
                        <Spinner color={"gray"}/>
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


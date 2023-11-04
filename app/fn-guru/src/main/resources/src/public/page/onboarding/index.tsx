import React, {FC, useEffect} from 'react'
import {Spinner} from "flowbite-react";
import {useCreateAnonymousAccount} from "../../../api/account.ts";
import {useApiPost, useAuth} from "../../../hook";
import {useLocation, useNavigate} from "react-router-dom";
import {NavigationScreenProp} from 'react-navigation';


const OnboardingPage: FC = () => {
    const location = useLocation()
    const {code} = location.state

    const navigate = useNavigate()
    const [account, isLoading, error] = useCreateAnonymousAccount()
    const [submitNamespace, namespaceSubmitted,] = useApiPost<{ namespaceId: string }>()
    const [auth] = useAuth()

    const [submitBlueprint, blueprintSubmitted] = useApiPost()

    useEffect(() => {
        if (account != null) {
            submitNamespace(`v1/groups/${auth.groupId}/namespaces`, {
                name: "Test-Name-Space",
                inputs: {}
            })
        }
    }, [account, auth, submitNamespace])

    useEffect(() => {
        if (namespaceSubmitted != null) {
            submitBlueprint(`v1/namespaces/${namespaceSubmitted.namespaceId}/adhoc`, {
                inputs: {},
                code: `sys = require('sys')
                sys.func.create({
                    name = 'Hello-World',
                    inputs = {},
                    code = [[ ${code} ]]
                })`
            })
        }

    }, [namespaceSubmitted, submitBlueprint]);


    useEffect(() => {
        if (blueprintSubmitted != null) {
            navigate('/namespaces')
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

export default OnboardingPage


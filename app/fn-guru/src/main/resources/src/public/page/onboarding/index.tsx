import React, {useEffect} from 'react'
import {Progress, Timeline} from "flowbite-react";
import {useCreateAnonymousAccount} from "../../../api/account.ts";
import {useApiPost, useAuth} from "../../../hook";

const OnboardingPage: React.FC = () => {
    const [account, isLoading, error] = useCreateAnonymousAccount()
    const [submitNamespace, namespaceSubmitted,] = useApiPost<unknown>()
    const [auth] = useAuth()
    
    useEffect(() => {
        if(account != null) {
            if (namespaceSubmitted != null) {
                // dispatch({type: 'namespace_created', namespaceId: namespaceSubmitted.namespaceId})
                console.log("Namespace created")
            } else {
                submitNamespace(`v1/groups/${auth.groupId}/namespaces`, {
                    name: "Test-Name-Space",
                    inputs: {}
                })
            }
        }
    }, [account])


    //     if (state.stage === 'NamespaceCreated') {
    //         submitFunc(`v1/namespaces/${state.namespaceId}/funcs`, {
    //             name: "Test-Name-Space",
    //             inputs: {},
    //             code: ""
    //         })
    //
    //         dispatch({type: 'function_created', funcId: 'test'})
    //     }
    //
    //
    //     useEffect(() => {
    //     if(account != null){
    //
    //         console.log("Execute blueprint here")
    //     }
    // }, [account]);

    return (
        <div className="flex flex-col h-screen justify-between">
            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <Progress progress={45}/>
                <div className="flex flex-col items-center justify-center">
                    <h1>Onboarding - </h1>

                    {JSON.stringify(account)}
                    {JSON.stringify(isLoading)}
                    {JSON.stringify(error)}

                    Your function will be deployed shortly

                    <Timeline>
                        <Timeline.Item>
                            <Timeline.Point/>
                            <Timeline.Content>
                                <Timeline.Title>
                                    Anonymous Account setup
                                </Timeline.Title>
                                <Timeline.Body>
                                    <p>
                                        TBD: Describe Account
                                    </p>
                                </Timeline.Body>
                            </Timeline.Content>
                        </Timeline.Item>
                        <Timeline.Item>
                            <Timeline.Point/>
                            <Timeline.Content>
                                <Timeline.Title>
                                    Namespace setup
                                </Timeline.Title>
                                <Timeline.Body>
                                    <p>
                                        TBD: Describe namespace
                                    </p>
                                </Timeline.Body>
                            </Timeline.Content>
                        </Timeline.Item>
                        <Timeline.Item>
                            <Timeline.Point/>
                            <Timeline.Content>
                                <Timeline.Title>
                                    Function setup
                                </Timeline.Title>
                                <Timeline.Body>
                                    <p>
                                        TBD: Describe function
                                    </p>
                                </Timeline.Body>
                            </Timeline.Content>
                        </Timeline.Item>
                    </Timeline>

                </div>
            </main>
        </div>
    );
}

export default OnboardingPage


import React, {useEffect} from 'react'
import {Progress, Timeline} from "flowbite-react";
import {createAnonymousAccount} from "../../../../api/account.ts";

import {ApiNamespaceCreateReq, createNamespace} from "../../../../api/namespace.ts";
import {createFunc, listGroup} from "../../../../api";
import {setAuth, storeAuth} from "../../../../auth.ts";
import {Auth} from "../../../../type.ts";
import {useNavigate} from "react-router-dom";

const OnboardingPage: React.FC = () => {
    // get account
    // create namespace
    // create function

    // createAnonymousAccount().then(r => {
    //     console.log(r.token)
    //     localStorage.setItem('auth', JSON.stringify({
    //         type: 'Anonymous',
    //         token: r.token
    //     }))
    // })

    const navigate = useNavigate()
    useEffect(() => {
        const run = async () => {
            try {
                // FIXME does an account already exist -> keep adding new namespace new opportunity to have more fun


                const anonymous = await createAnonymousAccount()

                const auth: Auth = {
                    type: 'Anonymous',
                    accountId: anonymous.accountId,
                    token: anonymous.token
                }

                storeAuth(auth)
                setAuth(auth)

                const groups = (await listGroup({limit: 1})).groups
                console.log(groups[0])


                const submitted_namespace = await createNamespace({
                    name: "a-new-beginning",
                    groupId: groups[0].id
                } as ApiNamespaceCreateReq)

                console.log(submitted_namespace)

                const func = await createFunc({
                    name: "function-one",
                    namespaceId: submitted_namespace['namespaceId']
                })

                console.log(func)

                navigate("/namespaces")

            } catch (e) {
                console.error(`failed to onboard - ${e}`)
            }

        }

        run()
    }, [])


    return (
        <div className="flex flex-col h-screen justify-between">
            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <Progress progress={45}/>
                <div className="flex flex-col items-center justify-center">
                    <h1>Onboarding</h1>

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


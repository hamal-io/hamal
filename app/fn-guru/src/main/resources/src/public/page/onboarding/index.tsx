import React, {useEffect, useReducer} from 'react'
import {Progress, Timeline} from "flowbite-react";
import {createAnonymousAccount} from "../../../api/account.ts";
import {useApiPost, useAuth} from "../../../hook";
import {ApiNamespace} from "../../../api/types";

export interface ApiNamespaceSubmitted {
    id: string;
    status: string;
    namespaceId: string;
}

const OnboardingPage: React.FC = () => {
    // get account
    // create namespace
    // create function

    // localStorage.removeItem(AUTH_STATE_KEY)

    type Stage = 'Initialised' | 'AccountCreated' | 'NamespaceCreated' | 'FunctionCreated'

    type State = {
        stage: Stage
        namespaceId: string
    }

    type Action =
        | { type: 'account_created', accountId: string, groupId: string }
        | { type: 'namespace_created', namespaceId: string }
        | { type: 'function_created', funcId: string }

    const reducer = (state: State, action: Action): State => {
        switch (action.type) {
            case "account_created":
                return {...state, stage: 'AccountCreated'}
            case 'namespace_created':
                return {...state, stage: 'NamespaceCreated', namespaceId: action.namespaceId}
            case 'function_created':
                return {...state, stage: 'FunctionCreated'}
        }
        return {...state}
    }

    const [state, dispatch] = useReducer(reducer, {stage: 'Initialised', namespaceId: ''});


    const [submitNamespace, namespaceSubmitted,] = useApiPost<ApiNamespaceSubmitted>()
    const [submitFunc, funcSubmitted,] = useApiPost()
    const [auth, setAuth] = useAuth()

    useEffect(() => {
        createAnonymousAccount().then(r => {
            console.log("TOKEN", r.token)
            localStorage.setItem('auth', JSON.stringify({
                type: 'Anonymous',
                accountId: r.accountId,
                groupId: r.groupIds[0],
                token: r.token
            }))

            console.log(`Created: ${JSON.stringify(auth)}`)
            console.log(`Created: ${JSON.stringify(r)}`)

            setAuth({
                type: 'Anonymous',
                accountId: r.accountId,
                groupId: r.groupIds[0],
                token: r.token
            })

            dispatch({type: 'account_created', accountId: r.accountId, groupId: r.groupIds[0]})
        })
    }, []);

    // useEffect(() => {
    //     console.log(JSON.stringify(auth))
    //
    // }, [auth]);


    useEffect(() => {
            if (state.stage === 'AccountCreated') {
                if (namespaceSubmitted != null) {
                    dispatch({type: 'namespace_created', namespaceId: namespaceSubmitted.namespaceId})
                } else {
                    console.log(JSON.stringify(auth))
                    submitNamespace(`v1/groups/${auth.groupId}/namespaces`, {
                        name: "Test-Name-Space",
                        inputs: {}
                    })
                }
            }


            if (state.stage === 'NamespaceCreated') {
                submitFunc(`v1/namespaces/${state.namespaceId}/funcs`, {
                    name: "Test-Name-Space",
                    inputs: {},
                    code: ""
                })

                dispatch({type: 'function_created', funcId: 'test'})
            }

            // console.log(`AUTH: ${JSON.stringify(auth)}`)
            //
            // if (auth != null && auth.type === 'Anonymous') {
            //     createNamespace(`v1/groups/${auth.groupId}/namespaces`, {
            //         name: "Test-Name-Space",
            //         inputs: {}
            //     })
        }, [state]
    )


    useEffect(() => {
        console.log("useApi", data)

    }, [data, namespace]);


// const navigate = useNavigate()
// useEffect(() => {
//     const run = async () => {
//         // try {
//         //     // FIXME does an account already exist -> keep adding new namespace new opportunity to have more fun
//         //
//         //
//         //     const anonymous = await createAnonymousAccount()
//         //
//         //     const auth: Auth = {
//         //         type: 'Anonymous',
//         //         accountId: anonymous.accountId,
//         //         token: anonymous.token
//         //     }
//         //
//         //     storeAuth(auth)
//         //     setAuth(auth)
//         //
//         //     const groups = (await listGroup({limit: 1})).groups
//         //     console.log(groups[0])
//         //
//         //
//         //     const submitted_namespace = await createNamespace({
//         //         name: "a-new-beginning",
//         //         groupId: groups[0].id
//         //     } as ApiNamespaceCreateReq)
//         //
//         //     console.log(submitted_namespace)
//         //
//         //     const func = await createFunc({
//         //         name: "function-one",
//         //         namespaceId: submitted_namespace['namespaceId']
//         //     })
//         //
//         //     console.log(func)
//         //
//         //     navigate("/namespaces")
//         //
//         // } catch (e) {
//         //     console.error(`failed to onboard - ${e}`)
//         // }
//
//     }
//
//     run()
// }, [])


    return (
        <div className="flex flex-col h-screen justify-between">
            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <Progress progress={45}/>
                <div className="flex flex-col items-center justify-center">
                    <h1>Onboarding - {state.stage} - {state.namespaceId}</h1>


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


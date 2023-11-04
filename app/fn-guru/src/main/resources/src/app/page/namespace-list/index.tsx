import React, {useEffect, useState} from 'react'
import {useNavigate} from "react-router-dom";
import {ApiNamespace, ApiNamespaceList} from "../../../api/types";
import {Button, Card, Label, Modal, TextInput} from "flowbite-react";
import {useApiGet, useApiPost, useAuth} from "../../../hook";

const NamespaceListPage: React.FC = () => {

    const navigate = useNavigate()
    const [auth] = useAuth()

    const [data, isLoading, error] = useApiGet<ApiNamespaceList>(`v1/groups/${auth.groupId}/namespaces`)


    if (isLoading) return "Loading..."
    if (error != null) return "Error -"


    const list = data.namespaces.filter(namespace => namespace.name !== "__default__").map(namespace => (
        <Card
            key={namespace.id}
            className="max-w-sm"
            onClick={() => navigate(`/namespaces/${namespace.id}`)}
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{namespace.name}</p>
            </h5>
            <p className="font-normal text-gray-700 dark:text-gray-400">
                TBD: Here is some description
            </p>
        </Card>
    ))

    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            <div className="flex p-3 items-center justify-center bg-white">
                <CreateNamespaceModalButton groupId={auth.groupId}/>
            </div>

            <div className="flex flex-col items-center justify-center">
                {list}
            </div>
        </main>
    );
}

// const Submit = (groupId: string, name: string) => {
//
//     const {data, isLoading, error} = useApi<ApiGroupList>({
//             method: "POST",
//             url: `v1/groups/${groupId}/namespaces`,
//             body: JSON.stringify({
//                     name: name,
//                     inputs: {},
//                 }
//             )
//         }
//     )
//     return (null)
// }

const CreateNamespaceModalButton = ({groupId}: { groupId: string }) => {
    const navigate = useNavigate()
    const [name, setName] = useState<string | undefined>()
    const [openModal, setOpenModal] = useState<string | undefined>();
    const props = {openModal, setOpenModal};

    useEffect(() => {
        const close = (e) => {
            if (e.keyCode === 27) {
                props.setOpenModal(undefined)
            }
        }
        window.addEventListener('keydown', close)
        return () => window.removeEventListener('keydown', close)
    }, [])

    const {post, data, error} = useApiPost<ApiNamespace>()
    useEffect(() => {
        if (data != null) {
            navigate(`/namespaces/${data.id}`)
            setOpenModal(undefined)
        }

        if (error != null) {
            console.log("error", error)
        }

    }, [data, navigate, error]);

    const submit = (post) => {
        post(`v1/groups/${groupId}/namespaces`, {name, inputs: {}})
    }

    return (
        <>
            <Button onClick={() => props.setOpenModal('default')}>New Namespace</Button>
            <Modal show={props.openModal === 'default'} onClose={() => props.setOpenModal(undefined)}>
                <Modal.Header>Create new namespace</Modal.Header>
                <Modal.Body>
                    <div className="space-y-6">
                        <div>
                            <div className="mb-2 block">
                                <Label htmlFor="name" value="Namespace name"/>
                            </div>
                            <TextInput id="name" placeholder="Useful namespace name..." required onChange={evt => setName(evt.target.value)}/>
                        </div>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button className={"w-full"} onClick={_ => submit(post)}>Create Namespace</Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}


export default NamespaceListPage;

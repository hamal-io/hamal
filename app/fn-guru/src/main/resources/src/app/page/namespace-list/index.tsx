import React, {useEffect, useState} from 'react'
import {useNavigate} from "react-router-dom";
import {ApiNamespace, ApiNamespaceList} from "../../../api/types";
import {Button, Card, Label, Modal, TextInput} from "flowbite-react";
import {useApiGet, useApiPost, useAuth} from "../../../hook";
import {HiPlus} from 'react-icons/hi';

const NamespaceListPage: React.FC = () => {

    const navigate = useNavigate()
    const [auth] = useAuth()

    const [data, isLoading, error] = useApiGet<ApiNamespaceList>(`v1/groups/${auth.groupId}/namespaces`)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    const list = data.namespaces.filter(namespace => namespace.name !== "__default__").map(namespace => (
        <Card
            key={namespace.id}
            className="w-full my-1 text-gray-900 hover:bg-gray-900  hover:text-gray-50 shadow-gray-200"
            onClick={() => navigate(`/namespaces/${namespace.id}`)}
        >
            <h2 className="text-lg tracking-tight">
                <p>{namespace.name}</p>
            </h2>
        </Card>
    ))

    return (
        <main className="flex-1 w-full pt-2 mx-auto text-lg h-full shadow-lg bg-gray-200">
            <section className="container p-4 mx-auto max-w-3xl">
                <div className="sm:flex sm:items-center sm:justify-between ">
                    <div>
                        <div className="flex items-center gap-x-3">
                            <h2 className="text-lg font-medium text-gray-800 dark:text-white">Namespaces</h2>
                        </div>
                        <p className="mt-1 text-sm text-gray-500 dark:text-gray-300">Organise your workflows</p>
                    </div>

                    <div className="flex items-center mt-4 gap-x-3">
                        <CreateNamespaceModalButton groupId={auth.groupId}/>
                    </div>
                </div>
            </section>

            <section className="container mx-auto max-w-3xl">
                <div className="flex flex-col py-6 items-center justify-center">
                    {list}
                </div>
            </section>
        </main>
    );
}

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

    const [post, data, isLoading, error] = useApiPost<ApiNamespace>()
    useEffect(() => {
        if (data != null) {
            navigate(`/namespaces/${data.id}`)
            setOpenModal(undefined)
        }

        if (error != null) {
            console.log("error", error)
        }

    }, [data, navigate, error]);


    return (
        <>
            <Button
                className={"bg-gray-400"}
                color={"dark"}
                outline
                onClick={() => props.setOpenModal('default')}>
                <HiPlus className="mr-2 h-5 w-5"/>
                Add Namespace
            </Button>

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
                    <Button className={"w-full"} onClick={() => {
                        post(`v1/groups/${groupId}/namespaces`, {name, inputs: {}})
                    }}>Create Namespace</Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}


export default NamespaceListPage;

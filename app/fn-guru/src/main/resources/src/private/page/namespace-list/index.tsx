import React, {useEffect, useState} from 'react'
import {useNavigate} from "react-router-dom";
import {ApiNamespaceSimple} from "../../../api/types";
import {Button, Card, Label, Modal, TextInput} from "flowbite-react";
import {createNamespace, listNamespace} from "../../../api/namespace.ts";
import {ApiGroupSimple, listGroup} from "../../../api";

const NamespaceListPage: React.FC = () => {


    const navigate = useNavigate()

    const [loading, setLoading] = useState(true)
    const [namespaces, setNamespaces] = useState([] as Array<ApiNamespaceSimple>)
    const [group, setGroup] = useState<ApiGroupSimple>()

    useEffect(() => {
        listGroup({limit: 1}).then(r => {
            setGroup(r.groups[0])
            listNamespace({groupId: r.groups[0].id, limit: 10}).then(response => {
                setNamespaces(response.namespaces)
                setLoading(false)
            })
        })
    }, []);

    if (loading) return "Loading..."


    const list = namespaces.map(namespace => (
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
                <CreateNamespaceModalButton group={group}/>
            </div>

            <div className="flex flex-col items-center justify-center">
                {list}
            </div>
        </main>
    );
}

const CreateNamespaceModalButton = ({group}: { group: ApiGroupSimple }) => {
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

    const submit = () => {
        createNamespace({
            groupId: group.id,
            name
        })
            .then(response => {
                navigate(`/namespaces/${response.id}`)
                props.setOpenModal(undefined)
            })
            .catch(console.error)
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
                    <Button className={"w-full"} onClick={submit}>Create Namespace</Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}


export default NamespaceListPage;

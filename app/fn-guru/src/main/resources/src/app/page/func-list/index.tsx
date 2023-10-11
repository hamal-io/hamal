import React, {useEffect, useState} from 'react'
import {ApiFuncSimple} from "../../../api/types";
import {createFunc, listFuncs} from "../../../api";
import {Button, Card, Label, Modal, TextInput} from "flowbite-react";
import {useNavigate} from "react-router-dom";

const FuncListPage: React.FC = () => {
    const navigate = useNavigate()

    const [loading, setLoading] = useState(true)
    const [funcs, setFuncs] = useState([] as Array<ApiFuncSimple>)
    useEffect(() => {
        listFuncs({limit: 10}).then(response => {
            setFuncs(response.funcs)
            setLoading(false)
        })
    }, []);

    if (loading) return "Loading..."


    const list = funcs.map(func => (
        <Card
            key={func.id}
            className="max-w-sm"
            onClick={() => navigate(`/functions/${func.id}`)}
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{func.name}</p>
            </h5>
            <p className="font-normal text-gray-700 dark:text-gray-400">
                TBD: Here is some description
            </p>
        </Card>
    ))

    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            <div className="flex p-3 items-center justify-center bg-white">
                <CreateFuncModalButton/>
            </div>

            <div className="flex flex-col items-center justify-center">
                {list}
            </div>
        </main>
    );
}


const CreateFuncModalButton = () => {
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
        createFunc({name})
            .then(response => {
                navigate(`/functions/${response.id}`)
                props.setOpenModal(undefined)
            })
            .catch(console.error)
    }

    return (
        <>
            <Button onClick={() => props.setOpenModal('default')}>New Function</Button>
            <Modal show={props.openModal === 'default'} onClose={() => props.setOpenModal(undefined)}>
                <Modal.Header>Create a new function</Modal.Header>
                <Modal.Body>
                    <div className="space-y-6">
                        <div>
                            <div className="mb-2 block">
                                <Label htmlFor="name" value="Function name"/>
                            </div>
                            <TextInput id="name" placeholder="Useful function name..." required onChange={evt => setName(evt.target.value)}/>
                        </div>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button className={"w-full"} onClick={submit}>Create Function</Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default FuncListPage


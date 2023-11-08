import {Avatar, Button, Dropdown, Label, Modal, Navbar as Delegate, TextInput} from "flowbite-react";
import React, {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {HiExclamation} from "react-icons/hi";
import {ApiAccountConversionSubmitted} from "@/api/account.ts";
import imgUrl from '@/assets/img/hamal.png'
import {useAuth} from "@/hook/auth.ts";
import {useApiPost} from "@/hook";


const Header: FC = () => {
    const navigate = useNavigate()
    const [auth, setAuth] = useAuth()
    const isAnonymous = auth.type === 'Anonymous'

    return (
        <Delegate
            fluid
            rounded
            className="bg-gray-200"
        >
            <Delegate.Brand>
            </Delegate.Brand>
            <div className="flex md:order-2">
                {isAnonymous && <ConvertAccountModalButton/>}

                {!isAnonymous && <div className="flex md:order-2">
                    <Dropdown
                        arrowIcon={false}
                        inline
                        label={<Avatar alt="User Icon" img={imgUrl} rounded/>}
                    >
                        <Dropdown.Header>
                        <span className="block text-sm">
                            Howdy, <span className="font-semibold">{auth.name} </span>
                        </span>
                        </Dropdown.Header>
                        {/*<DropdownItem onClick={() => {*/}
                        {/*    // FIXME core-72  - call logout endpoint to invalidate token*/}
                        {/*    setAuth(null)*/}
                        {/*    navigate("/")*/}
                        {/*}}>*/}
                        {/*    Log out*/}
                        {/*</DropdownItem>*/}
                    </Dropdown>
                    <Delegate.Toggle/>
                </div>
                }

            </div>
            <Delegate.Collapse>
                <Delegate.Link onClick={() => navigate("/play", {replace: true})}>
                    Play
                </Delegate.Link>
                <Delegate.Link onClick={() => navigate("/namespaces", {replace: true})}>
                    Namespaces
                </Delegate.Link>
            </Delegate.Collapse>
        </Delegate>
    );
}

const ConvertAccountModalButton = () => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [name, setName] = useState<string>(auth.name)
    const [password, setPassword] = useState<string>('')
    const [email, setEmail] = useState<string>('')
    const [openModal, setOpenModal] = useState<string | undefined>()
    const props = {openModal, setOpenModal}

    useEffect(() => {
        const close = (e) => {
            if (e.keyCode === 27) {
                props.setOpenModal(undefined)
            }
        }
        window.addEventListener('keydown', close)
        return () => window.removeEventListener('keydown', close)
    }, [])

    const [post, data] = useApiPost<ApiAccountConversionSubmitted>()
    useEffect(() => {
        if (data != null) {
            console.log("data", JSON.stringify(data))
            setAuth({
                type: 'User',
                accountId: auth.accountId,
                groupId: auth.groupId,
                token: data.token,
                name: data.name
            })
            navigate(`/namespaces`)
            setOpenModal(undefined)
        }
    }, [data, navigate]);


    return (
        <>
            <Button
                className="bg-red-400"
                onClick={() => props.setOpenModal('default')}>
                <HiExclamation className="mr-2 h-5 w-5"/>
                Protect this account from automatic deletion
            </Button>

            <Modal show={props.openModal === 'default'} onClose={() => props.setOpenModal(undefined)}>
                <Modal.Header>Register account</Modal.Header>
                <Modal.Body>
                    <div className="space-y-6">
                        <div>
                            <div className="mb-2 block">
                                <Label htmlFor="name" value="Username"/>
                            </div>
                            <TextInput
                                id="name"
                                placeholder="Your username"
                                required
                                onChange={evt => setName(evt.target.value)}
                                value={name}
                            />
                        </div>
                        <div>
                            <div className="mb-2 block">
                                <Label htmlFor="passwrod" value="Password"/>
                            </div>
                            <TextInput
                                id="passwrod"
                                placeholder="****"
                                type="password"
                                required
                                onChange={evt => setPassword(evt.target.value)}
                                value={password}
                            />
                        </div>
                        <div>
                            <div className="mb-2 block">
                                <Label htmlFor="email" value="Email (optional)"/>
                            </div>
                            <TextInput
                                id="email"
                                placeholder="name@email.com"
                                type="email"
                                onChange={evt => setEmail(evt.target.value)}
                                value={email}
                            />
                        </div>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button className={"w-full"} onClick={() => {
                        post(`v1/anonymous-accounts/${auth.accountId}/convert`, {name, password, email})
                    }}>Register</Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default Header;
import React, {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {HiExclamation} from "react-icons/hi";
import {ApiAccountConversionSubmitted} from "@/api/account.ts";
import imgUrl from '@/assets/img/hamal.png'
import {useAuth} from "@/hook/auth.ts";
import {useApiPost} from "@/hook";
import {Button} from "@/components/ui/button.tsx";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator, DropdownMenuShortcut,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Avatar, AvatarImage} from "@/components/ui/avatar.tsx";


const Header: FC = () => (
    <header className="sticky top-0 z-40 w-full border-b bg-background">
        <div className="container flex h-16 items-center space-x-4 sm:justify-between sm:space-x-0">
            <Nav/>
            <div className="flex flex-1 items-center justify-end space-x-4">
                <nav className="flex items-center space-x-1">
                </nav>
            </div>
        </div>
    </header>
);

function Nav() {
    const navigate = useNavigate()
    const [auth, setAuth] = useAuth()
    const isAnonymous = auth.type === 'Anonymous'
    return (
        <div className="flex gap-6 md:gap-10">
            <a key='home' href="/" className="flex items-center space-x-2">
                <span className="inline-block font-bold">fn(guru)</span>
            </a>
            <nav className="flex gap-6">
                {isAnonymous && <ConvertAccountModalButton/>}


                {!isAnonymous && <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Avatar>
                            <AvatarImage src={imgUrl}/>
                        </Avatar>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent className="w-56">
                        <DropdownMenuLabel>Howdy, {auth.name}</DropdownMenuLabel>
                        <DropdownMenuSeparator/>
                        <DropdownMenuGroup>
                            <DropdownMenuItem>
                                {/*<User className="mr-2 h-4 w-4" />*/}
                                <span>Profile</span>
                                <DropdownMenuShortcut>⇧⌘P</DropdownMenuShortcut>
                            </DropdownMenuItem>
                            <DropdownMenuSeparator/>
                            <DropdownMenuItem>
                                {/*<LogOut className="mr-2 h-4 w-4" />*/}
                                <span>Log out</span>
                                <DropdownMenuShortcut>⇧⌘Q</DropdownMenuShortcut>
                            </DropdownMenuItem>
                        </DropdownMenuGroup>
                    </DropdownMenuContent>
                </DropdownMenu>
                }
            </nav>
        </div>
    )
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

            {/*<Modal show={props.openModal === 'default'} onClose={() => props.setOpenModal(undefined)}>*/
            }
            {/*    <Modal.Header>Register account</Modal.Header>*/
            }
            {/*    <Modal.Body>*/
            }
            {/*        <div className="space-y-6">*/
            }
            {/*            <div>*/
            }
            {/*                <div className="mb-2 block">*/
            }
            {/*                    <Label htmlFor="name" value="Username"/>*/
            }
            {/*                </div>*/
            }
            {/*                <TextInput*/
            }
            {/*                    id="name"*/
            }
            {/*                    placeholder="Your username"*/
            }
            {/*                    required*/
            }
            {/*                    onChange={evt => setName(evt.target.value)}*/
            }
            {/*                    value={name}*/
            }
            {/*                />*/
            }
            {/*            </div>*/
            }
            {/*            <div>*/
            }
            {/*                <div className="mb-2 block">*/
            }
            {/*                    <Label htmlFor="passwrod" value="Password"/>*/
            }
            {/*                </div>*/
            }
            {/*                <TextInput*/
            }
            {/*                    id="passwrod"*/
            }
            {/*                    placeholder="****"*/
            }
            {/*                    type="password"*/
            }
            {/*                    required*/
            }
            {/*                    onChange={evt => setPassword(evt.target.value)}*/
            }
            {/*                    value={password}*/
            }
            {/*                />*/
            }
            {/*            </div>*/
            }
            {/*            <div>*/
            }
            {/*                <div className="mb-2 block">*/
            }
            {/*                    <Label htmlFor="email" value="Email (optional)"/>*/
            }
            {/*                </div>*/
            }
            {/*                <TextInput*/
            }
            {/*                    id="email"*/
            }
            {/*                    placeholder="name@email.com"*/
            }
            {/*                    type="email"*/
            }
            {/*                    onChange={evt => setEmail(evt.target.value)}*/
            }
            {/*                    value={email}*/
            }
            {/*                />*/
            }
            {/*            </div>*/
            }
            {/*        </div>*/
            }
            {/*    </Modal.Body>*/
            }
            {/*    <Modal.Footer>*/
            }
            {/*        <Button className={"w-full"} onClick={() => {*/
            }
            {/*            post(`v1/anonymous-accounts/${auth.accountId}/convert`, {name, password, email})*/
            }
            {/*        }}>Register</Button>*/
            }
            {/*    </Modal.Footer>*/
            }
            {/*</Modal>*/
            }
        </>
    )
}

export default Header;
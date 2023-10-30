import {Button, Checkbox, Label, TextInput} from 'flowbite-react';
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {login} from "../../../api/account.ts";
import {useAuth} from "../../../hook";

const LoginPage = () => {
    const navigate = useNavigate()
    const [username, setUsername] = useState<string>('')
    const [password, setPassword] = useState<string>('')

    const [auth, setAuth] = useAuth()
    return (
        <form className="flex max-w-md flex-col gap-4">
            <div>
                <div className="mb-2 block">
                    <Label
                        htmlFor="username"
                        value="Your username"
                    />
                </div>
                <TextInput
                    id="username"
                    placeholder="username"
                    required
                    type="username"
                    onChange={evt => setUsername(evt.target.value)}
                />
            </div>
            <div>
                <div className="mb-2 block">
                    <Label
                        htmlFor="password1"
                        value="Your password"
                    />
                </div>
                <TextInput
                    id="password1"
                    required
                    type="password"
                    placeholder={"**********"}
                    value={password}
                    onChange={evt => setPassword(evt.target.value)}
                />
            </div>
            <Button onClick={_ => {
                console.log("perform login")
                console.log("auth", auth)

                const action = async () => {
                    try {
                        const {accountId, groupIds, token} = await login(username, password)
                        console.log(accountId, token)
                        setAuth({
                            type: 'User',
                            accountId,
                            groupId: groupIds[0],
                            token
                        })

                        navigate("/namespaces")

                        console.log(auth)
                    } catch (e) {
                        console.log(`login failed - ${e}`)
                    }
                }
                action()

            }}>
                Sign in
            </Button>
        </form>
    )
}

export default LoginPage;

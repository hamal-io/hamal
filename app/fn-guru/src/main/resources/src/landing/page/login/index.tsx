import {Button, Checkbox, Label, TextInput} from 'flowbite-react';
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {login} from "../../../api/account.ts";
import {clearAuth, setAuth, storeAuth} from "../../../auth.ts";
import {Auth} from "../../../type.ts";

const handleLogin = async (username: string, password: string, remember: boolean, callback: () => void) => {
    try {
        clearAuth()

        const {accountId, token} = await login(username, password)
        console.log(token)

        const auth: Auth = {
            type: 'User',
            accountId: accountId,
            token: token
        }

        if (remember) {
            storeAuth(auth)
        }

        setAuth(auth)
        callback()
    } catch (e) {
        console.log(`login failed - ${e}`)
    }
}

const LoginPage = () => {
    const navigate = useNavigate()
    const [username, setUsername] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const [remember, setRemember] = useState(false)

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
            <div className="flex items-center gap-2">
                <Checkbox
                    id="remember"
                    value={remember}
                    onChange={evt => setRemember(evt.target.value)}
                />
                <Label htmlFor="remember">
                    Remember me
                </Label>
            </div>
            <Button onClick={_ => handleLogin(username, password, remember, () => {
                navigate("/namespaces")
            })}>
                Sign in
            </Button>
        </form>
    )
}

export default LoginPage;

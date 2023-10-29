import {Button, Checkbox, Label, TextInput} from 'flowbite-react';
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {login} from "../../../api/account.ts";
import useLocalStorageState from "use-local-storage-state";
import {AUTH_STATE_NAME, AuthState} from "../../../state.ts";

const handleLogin = async (username: string, password: string, remember: boolean, callback: () => void) => {
    try {
        // clearAuth()

        const {accountId, token} = await login(username, password)
        console.log(accountId, token)

        // const auth: Auth = {
        //     type: 'User',
        //     accountId: accountId,
        //     token: token
        // }
        //
        // if (remember) {
        //     storeAuth(auth)
        // }
        //
        // setAuth(auth)
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

    const [auth, setAuth] = useLocalStorageState<AuthState>(AUTH_STATE_NAME, {
        defaultValue: {
            type: 'Unauthorized',
            accountId: '',
            token: ''
        }
    })

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
            <Button onClick={_ => {
                console.log("perform login")
                console.log("auth", auth)

                const action = async () => {
                    try {
                        const {accountId, token} = await login(username, password)
                        console.log(accountId, token)
                        setAuth({
                            type: 'User',
                            accountId,
                            token
                        })

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

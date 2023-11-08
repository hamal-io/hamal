import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {login} from "@/api/account.ts";
import {useAuth} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";
import {Label} from "@/components/ui/label.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Icons} from "@/components/icon.tsx";

const LoginPage = () => {
    const navigate = useNavigate()
    const [isLoading, setLoading] = useState(false)

    const [username, setUsername] = useState<string>('')
    const [password, setPassword] = useState<string>('')

    const [auth, setAuth] = useAuth()
    return (
        <form className="flex max-w-md flex-col gap-4">
            <div>
                <div className="mb-2 block">
                    <Label
                        htmlFor="password1"
                    >
                        Your username
                    </Label>
                </div>
                <Input
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
                    >
                        Your password
                    </Label>
                </div>
                <Input
                    id="password1"
                    required
                    type="password"
                    placeholder={"**********"}
                    value={password}
                    onChange={evt => setPassword(evt.target.value)}
                />
            </div>
            <Button onClick={event => {
                event.preventDefault();
                setLoading(true);
                console.log("perform login")
                console.log("auth", auth)

                const action = async () => {
                    try {
                        const {accountId, groupIds, token, name} = await login(username, password)
                        console.log(accountId, token)
                        setAuth({
                            type: 'User',
                            accountId,
                            groupId: groupIds[0],
                            token,
                            name
                        })

                        navigate("/namespaces")

                        console.log(auth)
                    } catch (e) {
                        console.log(`login failed - ${e}`)
                    } finally {
                        setLoading(false);
                    }
                }
                action()

            }}>
                {isLoading && <Icons.spinner className="mr-2 h-4 w-4 animate-spin"/>}
                Sign in
            </Button>
        </form>
    )
}

export default LoginPage;

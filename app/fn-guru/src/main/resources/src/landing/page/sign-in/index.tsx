import {Button, Checkbox, Label, TextInput} from 'flowbite-react';
import {useNavigate} from "react-router-dom";

const SignInPage = () => {
    const navigate = useNavigate()
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
                    value={"root"}
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
                    value={"toor"}
                />
            </div>
            <div className="flex items-center gap-2">
                <Checkbox id="remember"/>
                <Label htmlFor="remember">
                    Remember me
                </Label>
            </div>
            <Button onClick={() => navigate("/dashboard")}>
                Sign in
            </Button>
        </form>
    )
}

export default SignInPage;

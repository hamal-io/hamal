import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {Button} from "@/components/ui/button.tsx";
import {useForm} from "react-hook-form"

import {ApiLoginSubmitted} from "@/types/auth";
import {Loader2} from "lucide-react";
import {useAuth} from "@/hook/auth.ts";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";


const formSchema = z.object({
    username: z.string().min(2).max(50),
    password: z.string().min(2).max(50)
})

export default function LoginPage() {
    const navigate = useNavigate()
    const [isLoading, setLoading] = useState(false)
    const [auth, setAuth] = useAuth()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            username: "",
            password: "",
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        console.log(values)

        try {

            const {accountId, groupIds, token, name} = await login(values.username, values.password)
            console.log(accountId, token)
            setAuth({
                type: 'User',
                accountId,
                groupId: groupIds[0],
                token,
                name
            })

            navigate("/namespaces", {replace: true})

            console.log(auth)
        } catch (e) {
            console.log(`login failed - ${e}`)
        } finally {
            setLoading(false)
        }

    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                <FormField
                    control={form.control}
                    name="username"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Username</FormLabel>
                            <FormControl>
                                <Input placeholder="username" {...field} />
                            </FormControl>
                            <FormDescription>
                                This is your public display name.
                            </FormDescription>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="password"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Password</FormLabel>
                            <FormControl>
                                <Input placeholder="***********" type="password" {...field} />
                            </FormControl>
                            <FormDescription>
                                This is your password.
                            </FormDescription>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <Button type="submit">Submit</Button>
            </form>
        </Form>
    );
}

async function login(username: string, password: string): Promise<ApiLoginSubmitted> {
    //FIXME do not use admin endpoint - only for prototyping
    const response = await fetch(`http://localhost:8008/v1/login`, {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        method: "POST",
        body: JSON.stringify({
            username,
            password
        })
    })

    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiLoginSubmitted;
}
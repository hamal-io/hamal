import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {Button} from "@/components/ui/button.tsx";
import {useForm} from "react-hook-form"

import {ApiLoginSubmitted} from "@/types/auth";
import {Loader2} from "lucide-react";
import {useAuth} from "@/hook/auth.ts";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {useApiLoginAccount} from "@/hook";


const formSchema = z.object({
    name: z.string().min(2).max(50),
    password: z.string().min(2).max(50)
})

export default function LoginPage() {
    const navigate = useNavigate()
    const [isLoading, setLoading] = useState(false)
    const [auth, setAuth] = useAuth()

    const [login, loginSubmitted] = useApiLoginAccount()

    useEffect(() => {
        if (loginSubmitted != null) {
            navigate("/dashboard", {replace: true})
            setLoading(false)
        }
    }, [loginSubmitted]);

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            password: "",
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        console.log(values)

        try {
            setLoading(true)
            login(values.name, values.password)
        } catch (e) {
            console.log(`login failed - ${e}`)
        } finally {
        }

    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                <FormField
                    control={form.control}
                    name="name"
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
                <Button type="submit">
                    {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                    Login
                </Button>
            </form>
        </Form>
    );
}

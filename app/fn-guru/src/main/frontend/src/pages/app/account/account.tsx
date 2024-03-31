import React, {useEffect, useState} from "react";
import {useAuth, useLogout} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";
import {useAccountUpdate} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import {Card} from "@/components/ui/card.tsx";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {FormControl, Form, FormField, FormItem, FormLabel} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Loader2} from "lucide-react";
import {useForm} from "react-hook-form";

const formSchema = z.object({
    email: z.string()/*.email("This is not a valid email.")*/,
    password: z.string()
})

const AccountPage = () => {
    const [auth] = useAuth()
    const [logout] = useLogout()
    const [update, updateRequested, , error] = useAccountUpdate()
    const [loading, setLoading] = useState(false)

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "user@hamal.io",
            password: "password"
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            const abortController = new AbortController()
            update(auth.accountId, values.email, values.password, abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.log(e)
        } finally {
            setLoading(false)
        }
    }

    if (error) return error
    if (loading) return loading

    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title={"Your Account"}
                    description={""}
                    actions={[
                        <Button variant={"destructive"} onClick={() => logout()}>Logout</Button>
                    ]}
                />
            </div>
            <div className="bg-white container h-full py-6">
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <FormField
                            control={form.control}
                            name="email"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Email</FormLabel>
                                    <FormControl>
                                        <Input {...field} />
                                    </FormControl>
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
                                        <Input {...field} />
                                    </FormControl>
                                </FormItem>
                            )}
                        />
                        <Button type={"submit"}>
                            {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                            Submit
                        </Button>
                    </form>
                </Form>
            </div>
        </div>
    )
}

export default AccountPage
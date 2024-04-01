import React, {FC, useEffect, useState} from "react";
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
import {Dialog, DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";


const AccountPage = () => {
    const [auth] = useAuth()
    const [logout] = useLogout()
    const [open, setOpen] = useState(false)
    const [dialogContent, setDialogContent] = useState(null)

    function handleEmail(){
        setDialogContent(
            <EmailForm accountId={auth.accountId} onClose={() => setOpen(false)}/>)
        setOpen(true)
    }

    function handlePassword() {
        setDialogContent(
            <PasswordForm accountId={auth.accountId} onClose={() => setOpen(false)}/>)
        setOpen(true)
    }

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
            <div className="bg-white container h-full py-6 items-center justify-center">
                <div className="flex flex-col gap-4">
                    <Button onClick={handleEmail} variant={"secondary"}>Change Email</Button>
                    <Button onClick={handlePassword} variant={"secondary"}>Change Password</Button>
                </div>
            </div>
            <Dialog open={open} onOpenChange={setOpen}>
                {dialogContent}
            </Dialog>
        </div>
    )
}

export default AccountPage

type FormProps = { accountId: string, onClose: () => void }
const EmailForm: FC<FormProps> = ({accountId, onClose}) => {
    const [update, updateRequested, , error] = useAccountUpdate()
    const [loading, setLoading] = useState(false)

    const formSchema = z.object({
            email: z.string().email("Please enter a valid email.")
        }
    )

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "user@hamal.io",
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            const abortController = new AbortController()
            update(accountId, values.email, null, abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.log(e)
        } finally {
            setLoading(false)
            onClose()
        }
    }

    return (

        <DialogContent>
            <DialogHeader>
                Change Email
            </DialogHeader>
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
                    <Button type={"submit"}>
                        {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                        Submit
                    </Button>
                </form>
            </Form>
        </DialogContent>

    )
}

const PasswordForm: FC<FormProps> = ({accountId, onClose}) => {
    const [update, updateRequested, , error] = useAccountUpdate()
    const [loading, setLoading] = useState(false)

    const formSchema = z.object({
            password: z.string().min(8)
        }
    )

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            password: "password"
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            const abortController = new AbortController()
            update(accountId, null, values.password, abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.log(e)
        } finally {
            setLoading(false)
            onClose()
        }
    }

    return (
        <DialogContent>
            <DialogHeader>
                Change Password
            </DialogHeader>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
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
                    <FormField
                        control={form.control}
                        name="password"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Confirm Password</FormLabel>
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
        </DialogContent>
    )
}



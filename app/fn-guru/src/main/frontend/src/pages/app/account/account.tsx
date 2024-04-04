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
                    <Button onClick={() => setOpen(true)} variant={"secondary"}>Change Password</Button>
                </div>
            </div>
            <Dialog open={open} onOpenChange={setOpen}>
                <PasswordForm accountId={auth.accountId} onClose={() => setOpen(false)}/>)
            </Dialog>
        </div>
    )
}

export default AccountPage

type FormProps = {accountId: string, onClose: () => void}
const PasswordForm: FC<FormProps> = ({accountId, onClose}) => {
    const [update, updateRequested, , error] = useAccountUpdate()
    const [loading, setLoading] = useState(false)

    const formSchema = z.object({
        currentPassword: z.string().min(4, "Password must be at least 4 characters").max(20),
        newPassword: z.string().min(4, "Password must be at least 4 characters").max(20),
        confirmPassword: z.string(),
    }).refine(data => data.newPassword === data.confirmPassword, {
        message: "Passwords don't match",
        path: ["confirmPassword"]
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),

    })

    function onSubmit(values: z.infer<typeof formSchema>, errors) {
        setLoading(true)
        try {
            const abortController = new AbortController()
            update(accountId, null, values.newPassword, abortController)
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
                        name="currentPassword"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Current Password</FormLabel>
                                <FormControl>
                                    <Input type={"password"}
                                        {...field} />
                                </FormControl>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="newPassword"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>New Password</FormLabel>
                                <FormControl>
                                    <Input type={"password"} {...field} />
                                </FormControl>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="confirmPassword"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Confirm Password</FormLabel>
                                <FormControl>
                                    <Input type={"password"} {...field} />
                                    {form.getFieldState("confirmPassword").error &&
                                        <span>{form.getFieldState("confirmPassword").error.message}</span>
                                    }
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

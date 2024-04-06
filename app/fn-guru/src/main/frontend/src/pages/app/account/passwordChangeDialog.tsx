import React, {FC, ReactNode, useEffect, useState} from "react";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Loader2} from "lucide-react";
import {useUpdatePassword} from "@/hook/auth.ts";


type Props = {
    children: ReactNode
}
const PasswordChangeDialog: FC<Props> = ({children}) => {
    const [open, setOpen] = useState(false)
    const [update, updateRequested] = useUpdatePassword()
    const [loading, setLoading] = useState(false)
    const [requestError, setRequestError] = useState(null)

    const passWordSchema = z.object({
        currentPassword: z.string().min(4, "Password must be at least 4 characters").max(20),
        newPassword: z.string().min(4, "Password must be at least 4 characters").max(20),
        confirmPassword: z.string(),
    }).refine(data => data.newPassword === data.confirmPassword, {
        message: "Passwords don't match",
        path: ["confirmPassword"]
    })

    type PasswordSchema = z.infer<typeof passWordSchema>

    const form = useForm<PasswordSchema>({
        resolver: zodResolver(passWordSchema),
        defaultValues: {
            currentPassword: "",
            newPassword: "",
            confirmPassword: ""
        }
    })


    function onSubmit(values: PasswordSchema) {
        setLoading(true)
        try {
            const abortController = new AbortController()
            update(values.currentPassword, values.newPassword, abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        if (updateRequested) {
            setLoading(false)
            if ("message" in updateRequested) {
                // @ts-ignore
                form.setError("currentPassword", updateRequested.message)
                setRequestError(updateRequested.message)
            } else {
                setOpen(false)
            }
        }
    }, [updateRequested])


    useEffect(() => {
        if (open === false) {
            setRequestError(null)
            form.reset()
        }
    }, [open]);


    const formErrors = form.formState.errors

    return (

        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                {children}
            </DialogTrigger>

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
                                        <p>
                                            <Input type={"password"} {...field} />
                                            {formErrors.currentPassword &&
                                                <span
                                                    className="text-red-500">{formErrors.currentPassword.message}</span>
                                            }
                                            {requestError &&
                                                <span className="text-red-500">{requestError}</span>
                                            }
                                        </p>
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
                                        <p>
                                            <Input type={"password"} {...field} />
                                            {formErrors.newPassword &&
                                                <span className="text-red-500">{formErrors.newPassword.message}</span>}
                                        </p>
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
                                        <p>
                                            <Input type={"password"} {...field} />
                                            {formErrors.confirmPassword &&
                                                <span
                                                    className="text-red-500">{formErrors.confirmPassword.message}</span>}
                                        </p>
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
        </Dialog>
    )
}

export default PasswordChangeDialog
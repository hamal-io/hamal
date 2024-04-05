import {useAccountPasswordChange} from "@/hook";
import React, {FC, useEffect, useState} from "react";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Loader2} from "lucide-react";


type Props = {
    onClose: () => void
}
const PasswordForm: FC<Props> = ({onClose}) => {
    const [update, updateRequested, requestLoading, requestError] = useAccountPasswordChange()
    const [loading, setLoading] = useState(false)

    const passWordSchema = z.object({
        currentPassword: z.string().min(4, "Password must be at least 4 characters").max(20).optional(),
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
            } else {
                onClose()
                form.reset()
            }
        }
    }, [updateRequested])

    const formErrors = form.formState.errors


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
                                    <p>
                                        <Input type={"password"} {...field} />
                                        {formErrors.currentPassword &&
                                            <span className="text-red-500">{formErrors.currentPassword.message}</span>}
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
                                            <span className="text-red-500">{formErrors.confirmPassword.message}</span>}
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
    )
}

export default PasswordForm
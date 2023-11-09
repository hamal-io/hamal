import React, {FC, useEffect, useState} from "react";
import {useAuth} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";

import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import {cn} from "@/utils";
import {useNavigate} from "react-router-dom";
import {useApiPost} from "@/hook";
import {ApiAccountConversionSubmitted} from "@/api/account.ts";
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";

const Header: FC = () => {
    const [auth] = useAuth()
    return (
        <div className="border-b">
            <div className="flex h-16 items-center px-4">
                {auth.type !== 'User' && <ConvertAccountModalButton/>}
                <MainNav className="mx-6"/>
                <div className="ml-auto flex items-center space-x-4">
                    <UserNav/>
                </div>
            </div>
        </div>
    )
}

export default Header


const UserNav = () => {
    const navigate = useNavigate()
    const [auth, setAuth] = useAuth()
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar className="h-8 w-8">
                        {/*<AvatarImage src="/avatars/01.png" alt="@shadcn"/>*/}
                        <AvatarFallback>{auth.name[0].toUpperCase()}</AvatarFallback>
                    </Avatar>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-32" align="end" forceMount>
                <DropdownMenuLabel className="font-normal">
                    <div className="flex flex-col space-y-1">
                        <p className="text-sm font-medium leading-none">{auth.name}</p>
                    </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator/>
                <DropdownMenuGroup>
                    <DropdownMenuItem>
                        Profile
                    </DropdownMenuItem>
                    <DropdownMenuItem>
                        Billing
                    </DropdownMenuItem>
                    <DropdownMenuItem>
                        Settings
                    </DropdownMenuItem>
                </DropdownMenuGroup>
                <DropdownMenuSeparator/>
                <DropdownMenuItem onClick={() => {
                    localStorage.removeItem(AUTH_KEY)
                    navigate("/", {replace: true})
                }}>
                    Log out
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}

export function MainNav({
                            className,
                            ...props
                        }: React.HTMLAttributes<HTMLElement>) {
    return (
        <nav
            className={cn("flex items-center space-x-4 lg:space-x-6", className)}
            {...props}
        >
            <a
                href="/dashboard"
                className="text-sm font-medium transition-colors hover:text-primary"
            >
                Dashboard
            </a>
            <a
                href="/playground"
                className="text-sm font-medium text-muted-foreground transition-colors hover:text-primary"
            >
                Playground
            </a>
            <a
                href="/namespaces"
                className="text-sm font-medium text-muted-foreground transition-colors hover:text-primary"
            >
                Namespaces
            </a>
        </nav>
    )
}


import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {AUTH_KEY} from "@/types/auth.ts";
import {Loader2} from "lucide-react";


const formSchema = z.object({
    name: z.string().min(2).max(50),
    password: z.string().min(2).max(50),
    email: z.string().min(2).max(50).optional()
})

const ConvertAccountModalButton = () => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)


    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            password: "",
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        console.log(values)

        try {
            post(`v1/anonymous-accounts/${auth.accountId}/convert`, {
                name: values.name,
                password: values.password,
                email: values.email
            })
            console.log(auth)
        } catch (e) {
            console.log(`login failed - ${e}`)
        } finally {
            // setLoading(false)
        }

    }

    const [post, data] = useApiPost<ApiAccountConversionSubmitted>()
    useEffect(() => {
        if (data != null) {
            console.log("data", JSON.stringify(data))
            setAuth({
                type: 'User',
                accountId: auth.accountId,
                groupId: auth.groupId,
                token: data.token,
                name: data.name
            })
        }
    }, [data]);


    useEffect(() => {
        console.log("AUTH", JSON.stringify(auth))
        if (auth != null && auth.type === 'User') {
            console.log("navigate")
            navigate(`/namespaces`)
            setOpenDialog(false)

        }
    }, [auth, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button className="bg-red-400">
                        Protect this account from automatic deletion
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Register account</DialogHeader>

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
                            <FormField
                                control={form.control}
                                name="email"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Email</FormLabel>
                                        <FormControl>
                                            <Input placeholder="your@email.io" type="email" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            This is your email(optional).
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Register
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}
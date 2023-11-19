import React, {FC, useEffect, useState} from "react";
import {unauthorized, useAuth} from "@/hook/auth.ts";
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

import {Avatar, AvatarFallback} from "@/components/ui/avatar.tsx";
import {cn} from "@/utils";
import {Link, useLocation, useNavigate} from "react-router-dom";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {Loader2} from "lucide-react";
import {ApiAccountConversionSubmitted} from "@/api/account.ts";
import {useAccountConvert, useApiPost} from "@/hook";
import {AUTH_KEY} from "@/types/auth.ts";


const Header: FC = () => {
    const [auth] = useAuth()
    return (
        <div className="border-b bg-white">
            <div className="flex h-16 px-4">
                <div className="w-full flex items-center justify-between space-x-4">
                    {auth.type !== 'User' ? <Convert/> : (<div/>)}
                    <Nav className="mx-6"/>
                    <Profile/>
                </div>
            </div>
        </div>
    )
}

export default Header

const Profile = () => {
    const navigate = useNavigate()
    const [auth] = useAuth()
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar className="h-8 w-8">
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
                <LogoutMenuItem/>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}

export interface ApiLogoutSubmitted {
    id: string;
    status: string;
    accountId: string;
}


const LogoutMenuItem = () => {
    const navigate = useNavigate()
    const [, setAuth] = useAuth()
    const [logout] = useApiPost<ApiLogoutSubmitted>()

    return (
        <DropdownMenuItem onClick={() => {
            logout('v1/logout', {})
            setAuth({...unauthorized})
            navigate("/")
        }}>
            Log out
        </DropdownMenuItem>
    )
}


const Nav = ({className, ...props}: React.HTMLAttributes<HTMLElement>) => {
    const location = useLocation()
    const currentPath = location.pathname

    const navigation: NavItem[] = [
        {
            href: `/dashboard`,
            label: "Dashboard",
            active: currentPath === '/dashboard'
        },
        {
            href: `/playground`,
            label: "Playground",
            active: currentPath === '/playground'
        },
        {
            href: `/flows`,
            label: "Flows",
            active: currentPath.startsWith('/flows')
        },
    ];

    return (
        <nav className="flex items-center space-x-4 lg:space-x-6">
            {navigation.map((item) => (<NavLink key={item.label} item={item}/>))}
        </nav>
    )
}

type NavItem = {
    href: string;
    external?: boolean;
    label: string;
    active?: boolean;
};

const NavLink: FC<{ item: NavItem }> = ({item}) => {
    return (
        <Link
            to={item.href}
            target={item.external ? "_blank" : undefined}
            className={cn("text-sm font-medium text-muted-foreground transition-colors hover:text-primary", {
                "text-primary": item.active
            })}>
            {item.label}
        </Link>
    );
};


const formSchema = z.object({
    name: z.string().min(2).max(50),
    password: z.string().min(2).max(50),
    email: z.string().min(2).max(50).optional()
})

const Convert = () => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)
    const [convert] = useAccountConvert()


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
            convert(values.name, values.password, values.email)
            console.log(auth)
        } catch (e) {
            console.log(`login failed - ${e}`)
        } finally {
            // setLoading(false)
        }

    }

    // const [post, data] = useApiPost<ApiAccountConversionSubmitted>()
    // useEffect(() => {
    //     if (data != null) {
    //         setAuth({
    //             type: 'User',
    //             accountId: auth.accountId,
    //             groupId: auth.groupId,
    //             defaultFlowIds: auth.defaultFlowIds,
    //             token: data.token,
    //             name: data.name
    //         })
    //     }
    // }, [data]);


    useEffect(() => {
        console.log("AUTH", JSON.stringify(auth))
        if (auth != null && auth.type === 'User') {
            console.log("navigate")
            navigate(`/flows`)
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
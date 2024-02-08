import React, {FC, useEffect, useState} from "react";
import {useAuth, useLogout} from "@/hook/auth.ts";
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
import {BookOpen, Loader2} from "lucide-react";
import {useAccountConvert} from "@/hook";


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
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar className="h-8 w-8">
                        <AvatarFallback>You</AvatarFallback>
                    </Avatar>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-32" align="end" forceMount>
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
    const [logout,] = useLogout()

    return (
        <DropdownMenuItem onClick={() => {
            logout()
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
            href: `/playground`,
            label: "Playground",
            active: currentPath === '/playground'
        },
        {
            href: `/namespaces`,
            label: "Namespaces",
            active: currentPath.startsWith('/namespaces')
        },
        {
            href: "https://docs.fn.guru",
            external: true,
            label: "Documentation",
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

const NavLink: FC<{
    item: NavItem
}> = ({item}) => {
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
    email: z.string().min(2).max(256),
    password: z.string().min(2).max(50)
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
            email: "",
            password: "",
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        try {
            convert(values.email, values.password)
        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }

    }

    useEffect(() => {
        if (auth != null && auth.type === 'User') {
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
                                name="email"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Email</FormLabel>
                                        <FormControl>
                                            <Input placeholder="your@email.guru" {...field} />
                                        </FormControl>
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
import {Link, useNavigate} from "react-router-dom";
import {Button, buttonVariants} from "@/components/ui/button.tsx";
import {cn} from "@/utils";
import {Icons} from "@/components/icon.tsx";
import {Input} from "@/components/ui/input.tsx";
import React, {useEffect, useState} from "react";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {useAccountLogin} from "@/hook";

export const LoginPage = () => {
    return (
        <>
            <div className="container relative hidden h-[800px] flex-col items-center justify-center md:grid lg:max-w-none lg:grid-cols-2 lg:px-0">
                <div className="absolute right-4 top-4 md:right-8 md:top-8">
                    <Button className={cn(buttonVariants({variant: "secondary"}), "")}>
                        Get started here
                    </Button>
                    <p className="space-y-2 text-xs text-gray-400 text-center">
                        No credit card required
                    </p>
                </div>

                <div className="relative hidden h-screen flex-col bg-muted p-10 text-white lg:flex">
                    <div className="absolute inset-0 bg-zinc-900"/>
                    <div className="relative z-20 flex items-center text-lg font-medium">
                        fn(guru)
                    </div>
                    <div className="relative z-20 mt-auto">
                        <blockquote className="space-y-2">
                            <p className="text-lg">
                                &ldquo;This service has saved me countless hours of work and
                                helped me deliver value to my customers faster than
                                ever before.&rdquo;
                            </p>
                            <footer className="text-sm">You</footer>
                        </blockquote>
                    </div>
                </div>
                <div className="lg:p-8">
                    <div className="mx-auto flex w-full flex-col justify-center space-y-6 sm:w-[350px]">
                        <div className="flex flex-col space-y-2 text-center">
                            <h1 className="text-2xl font-semibold tracking-tight">
                                Log into your account
                            </h1>
                            <p className="text-sm text-muted-foreground">
                                Fill out the form to log into your account.
                            </p>
                        </div>
                        <LoginForm/>
                        <div className="px-8 text-center text-sm text-muted-foreground">
                            By clicking continue, you agree to our
                            <div>
                                <Link to="/terms" className="underline underline-offset-4 hover:text-primary">
                                    Terms of Service
                                </Link>
                                <span className="px-1">and</span>
                                <Link to="/privacy" className="underline underline-offset-4 hover:text-primary">
                                    Privacy Policy
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default LoginPage


const LoginForm = () => {
    const navigate = useNavigate()
    const [login, loginSubmitted] = useAccountLogin()
    const [isLoading, setLoading] = useState<boolean>(false)

    const formSchema = z.object({
        name: z.string().min(1, 'Username or email can not be empty'),
        password: z.string()
            .min(1, 'Password can not be empty')
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            password: "",
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        try {
            setLoading(true)
            login(values.name, values.password)
        } catch (e) {
            console.error(e)
        } finally {
        }
    }

    useEffect(() => {
        if (loginSubmitted != null) {
            navigate("/flows", {replace: true})
            setLoading(false)
        }
    }, [loginSubmitted]);

    return (
        <div className={"grid gap-6"}>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                    <div className="grid gap-2">
                        <FormField
                            control={form.control}
                            name="name"
                            render={({field}) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            id="username"
                                            placeholder="username or email@fn.guru"
                                            type="name"
                                            autoCapitalize="none"
                                            autoCorrect="off"
                                            disabled={isLoading}
                                            {...field}
                                        />
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
                                    <FormControl>
                                        <Input
                                            id="password"
                                            placeholder="*********"
                                            type="password"
                                            autoCapitalize="none"
                                            autoCorrect="off"
                                            disabled={isLoading}
                                            {...field}
                                        />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />

                        <Button type="submit" disabled={isLoading}>
                            {isLoading && (
                                <Icons.spinner className="mr-2 h-4 w-4 animate-spin"/>
                            )}
                            Continue
                        </Button>
                    </div>
                </form>
            </Form>

            <div className="relative">
                <div className="absolute inset-0 flex items-center">
                    <span className="w-full border-t"/>
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                    <span className="bg-background px-2 text-muted-foreground">
                        Or continue with
                    </span>
                </div>
            </div>
            <Button variant="outline" type="button" disabled={isLoading}>
                {isLoading ? (
                    <Icons.spinner className="mr-2 h-4 w-4 animate-spin"/>
                ) : (
                    <Icons.metamask className="mr-2 h-4 w-4"/>
                )}
                <span className="pl-2">Metamask</span>
            </Button>
        </div>
    )
}
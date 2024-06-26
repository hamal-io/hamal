import {Link, useNavigate} from "react-router-dom";
import {Button, buttonVariants} from "@/components/ui/button.tsx";
import {cn} from "@/utils";
import {Icons} from "@/components/icon.tsx";
import {Input} from "@/components/ui/input.tsx";
import React, {FC, useEffect, useState} from "react";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {useAccountLogin} from "@/hook";
import {Buffer} from "buffer";
import {MetaMaskUIProvider, useSDK} from "@metamask/sdk-react-ui";
import {useMetaMaskChallenge, useMetaMaskToken} from "@/hook/auth.ts";

export const LoginPage = () => {
    return (
        <MetaMaskUIProvider debug={false} sdkOptions={{
            checkInstallationImmediately: false,
            dappMetadata: {
                name: "fn(guru)",
                url: window.location.host,
            }
        }}>
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
        </MetaMaskUIProvider>
    )
}

export default LoginPage

type MetaMaskButtonProps = {
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
}


const MetaMaskButton: FC<MetaMaskButtonProps> = ({loading, setLoading}) => {
    const navigate = useNavigate()
    const {sdk} = useSDK();
    const [address, setAddress] = useState<string | null>(null)
    const [requestChallenge, challenge] = useMetaMaskChallenge()
    const [requestToken, token] = useMetaMaskToken()

    useEffect(() => {
        if (challenge != null) {
            console.log("init challenge", challenge)
            const invoke = async () => {
                // @ts-ignore
                const signature = await ethereum.request({
                    method: 'personal_sign',
                    params: [challenge, address],
                });
                console.log("signature", signature)

                requestToken(address, signature)
            }

            invoke()
        }
    }, [challenge]);

    useEffect(() => {
        if (token != null) {
            navigate("/dashboard", {replace: true})
        }
    }, [token]);

    const connect = async () => {
        try {
            setLoading(true)
            const address = (await sdk?.connect())?.[0];
            setAddress(address)
            requestChallenge(address)
        } catch (err) {
            console.error(`failed to connect..`, err);
        } finally {
            setLoading(false)
        }
    };

    return (
        <Button variant="outline" type="button" disabled={loading} onClick={connect}>
            {loading ? (
                <Icons.spinner className="mr-2 h-4 w-4 animate-spin"/>
            ) : (
                <Icons.metamask className="mr-2 h-4 w-4"/>
            )}
            <span className="pl-2">Metamask</span>
        </Button>
    )
}

const LoginForm = () => {
    const navigate = useNavigate()
    const [login, loginSubmitted] = useAccountLogin()
    const [isLoading, setLoading] = useState<boolean>(false)

    const formSchema = z.object({
        email: z.string().min(1, 'Email can not be empty'),
        password: z.string()
            .min(1, 'Password can not be empty')
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "",
            password: "",
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        try {
            setLoading(true)
            login(values.email, values.password)
        } catch (e) {
            console.error(e)
        }
    }

    useEffect(() => {
        if (loginSubmitted != null) {
            navigate(`/dashboard`, {replace: true})
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
                            name="email"
                            render={({field}) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            id="email"
                                            placeholder="email@fn.guru"
                                            type="email"
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
            <MetaMaskButton
                loading={isLoading}
                setLoading={setLoading}
            />
        </div>
    )
}
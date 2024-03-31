import React, {FC, forwardRef, HTMLAttributes, ReactNode, useEffect, useState} from "react";
import facebookSvg from "@/images/Facebook.svg";
import twitterSvg from "@/images/Twitter.svg";
import googleSvg from "@/images/Google.svg";
import Input from "@/components/shared/Input/Input";
import ButtonPrimary from "@/components/shared/Button/ButtonPrimary";
import {MetaMaskProvider, useSDK} from "@metamask/sdk-react";
import ImageW from "@/types/image.tsx";
import LinkW from "@/types/link.tsx";
import {useNavigate} from "react-router-dom";
import {useMetaMaskChallenge, useMetaMaskToken} from "@/hooks/auth.ts";
import Button from "@/components/shared/Button/Button.tsx";
import {IconProps, Icons} from "@/pages/landing/components/icons.tsx";

const loginSocials = [
    {
        name: "Continue with Facebook",
        href: "#",
        icon: facebookSvg,
    },
    {
        name: "Continue with Twitter",
        href: "#",
        icon: twitterSvg,
    },
    {
        name: "Continue with Google",
        href: "#",
        icon: googleSvg,
    },
];


const LoginPage = () => {
    const [loading, setLoading] = useState(false)

    return (
        <div className={`nc-PageLogin`} data-nc-id="PageLogin">
            <div className="container mb-24 lg:mb-32">
                <h2 className="my-20 flex items-center text-3xl leading-[115%] md:text-5xl md:leading-[115%] font-semibold text-neutral-900 dark:text-neutral-100 justify-center">
                    Login
                </h2>
                <div className="max-w-md mx-auto space-y-6">
                    <div className="grid gap-3">
                        {loginSocials.map((item, index) => (

                            <LoginButton key={index} name={item.name} icon={item.icon} href={item.href}></LoginButton>

                        ))}

                        <MetaMaskProvider debug={false} sdkOptions={{
                            checkInstallationImmediately: false,
                            dappMetadata: {
                                name: "fn(guru)",
                                url: window.location.host,
                            }
                        }}>


                            <MetaMaskButton loading={loading} setLoading={setLoading}/>
                        </MetaMaskProvider>


                    </div>
                    {/* OR */}
                    <div className="relative text-center">
            <span
                className="relative z-10 inline-block px-4 font-medium text-sm bg-white dark:text-neutral-400 dark:bg-neutral-900">
              OR
            </span>
                        <div
                            className="absolute left-0 w-full top-1/2 transform -translate-y-1/2 border border-neutral-100 dark:border-neutral-800"></div>
                    </div>
                    {/* FORM */}
                    <form className="grid grid-cols-1 gap-6" action="#" method="post">
                        <label className="block">
              <span className="text-neutral-800 dark:text-neutral-200">
                Email address
              </span>
                            <Input
                                type="email"
                                placeholder="example@example.com"
                                className="mt-1"
                            />
                        </label>
                        <label className="block">
              <span className="flex justify-between items-center text-neutral-800 dark:text-neutral-200">
                Password
                <LinkW href="/signup" className="text-sm text-green-600">
                  Forgot password?
                </LinkW>
              </span>
                            <Input type="password" className="mt-1"/>
                        </label>
                        <ButtonPrimary type="submit">Continue</ButtonPrimary>
                    </form>

                    {/* ==== */}
                    <span className="block text-center text-neutral-700 dark:text-neutral-300">
            New user? {` `}
                        <LinkW className="text-green-600" href="/signup">
              Create an account
            </LinkW>
          </span>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;

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
            const msg = `0x${Buffer.from("test", 'utf8').toString('hex')}`;
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
            navigate("/", {replace: true})
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
        <button
            disabled={loading}
            className="flex w-full rounded-lg bg-primary-50 dark:bg-neutral-800 px-4 py-3 transform transition-transform sm:px-6 hover:translate-y-[-2px]"
            onClick={connect}
        >
            {loading ? <Icons.spinner className="mr-2 h-4 w-4 animate-spin"/> :
                <Icons.metamask className="mr-2 h-4 w-4"/>}
            <h3 className="flex-grow text-center text-sm font-medium text-neutral-700 dark:text-neutral-300 sm:text-sm">
                {"Continue with Metamask"}
            </h3>
        </button>
    )
}

type LoginButtonProps = {
    name: string
    icon: React.ForwardRefExoticComponent<HTMLAttributes<SVGElement>>
}
const LoginButton = forwardRef<HTMLButtonElement, LoginButtonProps>(
    function (props, ref) {
        const {name, icon} = props;
        return (
            <button
                ref={ref}
                className="flex w-full rounded-lg bg-primary-50 dark:bg-neutral-800 px-4 py-3 transform transition-transform sm:px-6 hover:translate-y-[-2px]"
                {...props}
            >
                <ImageW
                    className="flex-shrink-0"
                    src={icon}
                    alt={name}
                    sizes="40px"
                />
                <h3 className="flex-grow text-center text-sm font-medium text-neutral-700 dark:text-neutral-300 sm:text-sm">
                    {name}
                </h3>
            </button>
        )
    })

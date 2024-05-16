import React, {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useSDK} from "@metamask/sdk-react-ui";
import {useMetaMaskChallenge, useMetaMaskToken} from "@/hook/auth.ts";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";

type MetaMaskButtonProps = {
    className?: string;
}

export const MetaMaskButton: FC<MetaMaskButtonProps> = ({className}) => {
    const [connecting, setConnecting] = useState(false)
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
            setConnecting(true)
            const address = (await sdk?.connect())?.[0];
            setAddress(address)
            requestChallenge(address)
        } catch (err) {
            console.error(`failed to connect..`, err);
            setConnecting(false)
        }
    };

    return (
        <ButtonPrimary
            className={`self-center ${className}`}
            onClick={connect}
            loading={connecting}
            sizeClass="px-4 py-2 sm:px-5"
        >
            Connect Wallet
        </ButtonPrimary>
    )
}
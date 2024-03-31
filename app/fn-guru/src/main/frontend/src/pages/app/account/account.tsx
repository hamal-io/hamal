import React, {useEffect} from "react";
import {useAuth} from "@/hook/auth.ts";


const AccountPage = () => {
    const [auth] = useAuth()


    return (
        <div>
            <p>
                {auth.accountId}
            </p>
        </div>
    )
}

export default AccountPage
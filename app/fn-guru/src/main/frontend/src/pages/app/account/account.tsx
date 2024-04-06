import React, {useState} from "react";
import {useAuth, useLogout} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import UpdatePasswordDialog from "@/pages/app/account/updatePasswordDialog.tsx";


const AccountPage = () => {
    const [auth] = useAuth()
    const [logout] = useLogout()
    const [open, setOpen] = useState(false)


    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title={"Your Account"}
                    description={""}
                    actions={[
                        <Button variant={"destructive"} onClick={() => logout()}>Logout</Button>
                    ]}
                />
            </div>
            <div className="bg-white container h-full py-6 items-center justify-center">
                <div className="flex flex-col gap-4">
                    <UpdatePasswordDialog>
                        <Button onClick={() => setOpen(true)} variant={"secondary"}>Change Password</Button>
                    </UpdatePasswordDialog>
                </div>
            </div>


        </div>
    )
}

export default AccountPage



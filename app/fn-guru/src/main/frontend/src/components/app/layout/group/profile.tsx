import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Avatar, AvatarFallback} from "@/components/ui/avatar.tsx";
import {useLogout} from "@/hook/auth.ts";
import React from "react";

const LogoutMenuItem = () => {
    const [logout,] = useLogout()
    return (
        <DropdownMenuItem onClick={() => {logout()}}>
            Log out
        </DropdownMenuItem>
    )
}

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

export default Profile
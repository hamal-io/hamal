import React from "react";
import logoImg from "@/images/logo_256.png";

export interface LogoProps {
    className?: string;
}

const Logo: React.FC<LogoProps> = ({className = "block h-16 w-auto flex-shrink-0",}) => {
    return (
        <div className="flex flex-row items-center">
            <img src={logoImg} className={`${className}`} alt={"logo"}/>
            <h1 className="text-2xl font-semibold">nyanbot</h1>
        </div>
    )
};

export default Logo;

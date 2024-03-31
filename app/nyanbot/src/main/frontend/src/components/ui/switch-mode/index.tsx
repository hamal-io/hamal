import React from "react";
import {MoonIcon} from "@heroicons/react/24/solid";
import {SunIcon} from "@heroicons/react/24/outline";
import {useChangeTheme, useUiState} from "@/hooks/ui.ts";

export interface SwitchDarkModeProps {
    className?: string;
}

const SwitchMode: React.FC<SwitchDarkModeProps> = ({className = ""}) => {
    const [uiState] = useUiState()
    const [changeTheme] = useChangeTheme()
    return (
        <button
            className={`self-center text-2xl md:text-3xl w-12 h-12 rounded-full text-neutral-700 dark:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-800 focus:outline-none flex items-center justify-center ${className}`}
            onClick={changeTheme}
        >
            <span className="sr-only">Enable dark mode</span>
            {uiState.theme === 'dark' ? (
                <MoonIcon className="w-7 h-7" aria-hidden="true"/>
            ) : (
                <SunIcon className="w-7 h-7" aria-hidden="true"/>
            )}
        </button>
    );
};

export default SwitchMode;

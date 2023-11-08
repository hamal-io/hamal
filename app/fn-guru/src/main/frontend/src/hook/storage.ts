import React, {useState, useEffect, SetStateAction} from "react";

export const useLocalStorage = <T>(key: string, defaultValue: T): [T | null, React.Dispatch<SetStateAction<T>>] => {
    const [value, setValue] = useState<T | null>(() => {
        let currentValue: T;
        try {
            currentValue = JSON.parse(
                localStorage.getItem(key) || String(defaultValue)
            );
        } catch (error) {
            currentValue = defaultValue;
        }

        return currentValue;
    });

    useEffect(() => {
        if (value) {
            localStorage.setItem(key, JSON.stringify(value))
        } else {
            localStorage.removeItem(key)
        }
    }, [key, value])

    return [value, setValue];
};


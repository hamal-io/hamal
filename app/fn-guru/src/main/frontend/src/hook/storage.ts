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
        if (value != null) {
            localStorage.setItem(key, JSON.stringify(value))
        }
    }, [key, value])

    return [value, setValue];
};


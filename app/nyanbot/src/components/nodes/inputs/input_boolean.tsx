import React, {FC, useState} from "react"
import styes from "./input_boolean.module.css"

type InputBooleanProps = {
    value?: boolean
    onChange?: (value: boolean) => void
}

export const InputBoolean: FC<InputBooleanProps> = ({value, onChange}) => {
    return (
        <>
            <input
                type="checkbox"
                checked={value}
                onChange={(event) => {
                    onChange(event.target.checked)
                }}
            />
        </>
    )
}
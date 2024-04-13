import React, {FC, useState} from "react";
import {InputSelect, InputText} from "./input";
import styles from "@/components/nodes/port.module.css";
import {PortInputWidget} from "@/components/nodes/port.tsx";
import {Control, isControlCondition, isControlInit, isControlInput, isControlText, PortInput} from "@/components/nodes/types.ts";

type ControlsProps = {
    controls: Control[]
}

export const ControlListWidget: FC<ControlsProps> = ({controls}) => {
    return (
        <div className={styles.wrapper} data-component="ports">
            {
                controls.map((control) => {

                    if (isControlCondition(control)) {
                        return <ControlConditionWidget/>
                    }

                    if (isControlInput(control)) {
                        return <ControlInputWidget/>
                    }

                    if (isControlInit(control)) {
                        return <ControlInitWidget description={control.description}/>
                    }

                    if (isControlText(control)) {
                        return <ControlTextWidget port={control.port} placeholder={control.placeholder} text={control.text}/>
                    }

                    throw `Not supported yet`
                })
            }
        </div>
    )
}


type ControlInitWidgetProps = {
    description?: string;
}

export const ControlInitWidget: FC<ControlInitWidgetProps> = ({description}) => {
    return (
        <div className="flex flex-row">
            <span className="text-fuchsia-400">{description}</span>
        </div>
    )
}


type ControlTextWidgetProps = {
    placeholder?: string;
    port?: PortInput;
    text?: string;
}

export const ControlTextWidget: FC<ControlTextWidgetProps> = ({placeholder, port, text}) => {
    return (
        <div className="flex flex-row">
            {port && <PortInputWidget/>}
            <InputText type={'text'} value={text} placeholder={placeholder}/>
        </div>
    )
}

type ControlConditionWidgetProps = {}

export const ControlConditionWidget: FC<ControlConditionWidgetProps> = ({}) => {
    const [operator, setOperator] = useState('test')
    return (
        <div className="flex flex-col">
            <div className="flex flex-row">
                {/*<PortInputWidget/>*/}
                <InputText type={'text'} placeholder={'test'}/>
            </div>
            <div className="flex flex-row">
                {/*<PortInputWidget/>*/}
                <InputSelect options={[
                    {
                        description: 'test',
                        value: 'test',
                        sortIndex: 1,
                        label: 'label'
                    },
                    {
                        description: 'test 2',
                        value: 'test2',
                        sortIndex: 2,
                        label: 'label 2'
                    }
                ]}
                             data={operator}
                             onChange={setOperator}
                             placeholder={'test'}/>;
            </div>
            <div className="flex flex-row">
                {/*<PortInputWidget/>*/}
                <InputText type={'text'} placeholder={'test'}/>;
            </div>
        </div>
    )
}

type ControlInputWidgetProps = {}

export const ControlInputWidget: FC<ControlInputWidgetProps> = ({}) => {
    return (
        <div className="flex flex-row">
            <PortInputWidget/>
        </div>
    )
}


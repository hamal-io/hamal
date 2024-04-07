import React, {FC, useState} from "react";
import {InputSelect, InputText} from "./input";
import styles from "@/components/nodes/port.module.css";
import {PortInputWidget} from "@/components/nodes/port.tsx";
import {Control, isControlCondition, isControlInput, isControlText, Port} from "@/components/nodes/types.ts";

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

                    if (isControlText(control)) {
                        return <ControlTextWidget ports={control.ports} placeholder={control.placeholder}/>
                    }

                    throw `Not supported yet`
                })
            }
        </div>
    )
}


type ControlTextWidgetProps = {
    placeholder?: string;
    ports: Port[];
    text?: string;
}

export const ControlTextWidget: FC<ControlTextWidgetProps> = ({placeholder, ports, text}) => {
    return (
        <div className="flex flex-row">
            {ports.length > 0 && <PortInputWidget/>}
            <InputText type={'text'} value={text} placeholder={placeholder}/>;
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
                <InputText type={'text'} placeholder={'test'}/>;
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


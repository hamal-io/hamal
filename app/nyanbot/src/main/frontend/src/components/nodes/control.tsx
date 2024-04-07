import React, {FC, useState} from "react";
import {InputSelect, InputText} from "./input";
import styles from "@/components/nodes/port.module.css";
import {PortInput} from "@/components/nodes/port.tsx";

type ControlsProps = {}

export const Controls: FC<ControlsProps> = ({}) => {
    return (
        <div className={styles.wrapper} data-component="ports">
            {/*<ControlConditionWidget/>*/}
            <ControlInputWidget/>
        </div>
    )
}


type ControlTextWidgetProps = {}

export const ControlTextWidget: FC<ControlTextWidgetProps> = ({}) => {
    return (
        <div className="flex flex-row">
            <PortInput/>
            <InputText type={'text'} placeholder={'test'}/>;
        </div>
    )
}

type ControlConditionWidgetProps = {}

export const ControlConditionWidget: FC<ControlConditionWidgetProps> = ({}) => {
    const [operator, setOperator] = useState('test')
    return (
        <div className="flex flex-col">
            <div className="flex flex-row">
                <PortInput/>
                <InputText type={'text'} placeholder={'test'}/>;
            </div>
            <div className="flex flex-row">
                <PortInput/>
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
                <PortInput/>
                <InputText type={'text'} placeholder={'test'}/>;
            </div>
        </div>
    )
}

type ControlInputWidgetProps = {}

export const ControlInputWidget: FC<ControlInputWidgetProps> = ({}) => {
    return (
        <div className="flex flex-row">
            <PortInput/>
        </div>
    )
}


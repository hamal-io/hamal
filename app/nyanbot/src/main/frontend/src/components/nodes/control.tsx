import React, {FC, useContext, useState} from "react";
import {InputSelect, InputText} from "./inputs";
import styles from "@/components/nodes/port.module.css";
import {PortInputWidget} from "@/components/nodes/port.tsx";
import {Control, ControlInvoke, ControlText, isControlCondition, isControlInit, isControlInput, isControlInvoke, isControlText} from "@/components/nodes/types.ts";
import {ContextEditorState} from "@/components/nodes/editor.tsx";

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

                    if (isControlInvoke(control)) {
                        return <ControlInvokeWidget control={control}/>
                    }

                    if (isControlText(control)) {
                        return <ControlTextWidget control={control}/>
                    }

                    throw `Not supported yet`
                })
            }
        </div>
    )
}

type ControlInvokeWidgetProps = {
    control: ControlInvoke;
}

export const ControlInvokeWidget: FC<ControlInvokeWidgetProps> = ({}) => {
    return (
        <div className="flex flex-row">
            <PortInputWidget/>
            <span> Invoke</span>
        </div>
    );
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
    control: ControlText;
}

export const ControlTextWidget: FC<ControlTextWidgetProps> = ({control}) => {
    const {id, port, defaultValue, placeholder} = control;
    const {dispatch} = useContext(ContextEditorState)
    const {state} = useContext(ContextEditorState)

    return (
        <div className="flex flex-row">
            {port && <PortInputWidget/>}
            <InputText type={'text'} value={state.controls[id].defaultValue} placeholder={placeholder} onChange={(value) =>
                dispatch({type: 'CONTROL_TEXT_UPDATED', id: control.id, value})
            }/>
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


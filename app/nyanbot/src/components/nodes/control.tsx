import React, {FC, useContext, useState} from "react";
import {InputSelect, TextArea} from "./inputs";
import styles from "@/components/nodes/port.module.css";
import {PortInputWidget} from "@/components/nodes/port.tsx";
import {
    ControlId,
    ControlInvoke,
    ControlInputString,
    isControlCondition,
    isControlInput,
    isControlInputBoolean,
    isControlInvoke,
    isControlInputString,
    Node
} from "@/components/nodes/types.ts";
import {ContextEditorState} from "@/components/nodes/editor.tsx";
import {InputBoolean} from "@/components/nodes/inputs/input_boolean.tsx";

type ControlsProps = {
    node: Node;
}

export const ControlListWidget: FC<ControlsProps> = ({node}) => {
    const {controls, nodeControlIds} = useContext(ContextEditorState).state;
    return (
        <div key={node.id} className={styles.wrapper} data-component="ports">
            {
                nodeControlIds[node.id].map(controlId => controls[controlId]).map((control) => {

                    if (isControlInputBoolean(control)) {
                        return <ControlInputBooleanWidget key={control.id} id={control.id}/>
                    }

                    if (isControlCondition(control)) {
                        return <ControlConditionWidget key={control.id}/>
                    }

                    if (isControlInput(control)) {
                        return <ControlInputWidget key={control.id}/>
                    }

                    if (isControlInvoke(control)) {
                        return <ControlInvokeWidget key={control.id} control={control}/>
                    }

                    if (isControlInputString(control)) {
                        return <ControlInputStringWidget key={control.id} control={control}/>
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

export const ControlInvokeWidget: FC<ControlInvokeWidgetProps> = ({control}) => {
    return (
        <div className="flex flex-row">
            <PortInputWidget port={control.port}/>
            <span> Invoke</span>
        </div>
    );
}


type ControlInputStringWidgetProps = {
    control: ControlInputString;
}

export const ControlInputStringWidget: FC<ControlInputStringWidgetProps> = ({control}) => {
    const {dispatch} = useContext(ContextEditorState)

    return (
        <div className="flex flex-row">
            {control.port && <PortInputWidget port={control.port}/>}
            <TextArea value={control.value} placeholder={control.placeholder} onChange={(value) =>
                dispatch({type: 'CONTROL_INPUT_STRING_UPDATED', id: control.id, value})
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
                <TextArea placeholder={'test'}/>
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
                <TextArea placeholder={'test'}/>;
            </div>
        </div>
    )
}

type ControlInputWidgetProps = {}

export const ControlInputWidget: FC<ControlInputWidgetProps> = ({}) => {
    return (
        <div className="flex flex-row">
            FIXME
            {/*<PortInputWidget/>*/}
        </div>
    )
}


type ControlInputBooleanWidgetProps = {
    id: ControlId
}

export const ControlInputBooleanWidget: FC<ControlInputBooleanWidgetProps> = ({id}) => {
    const {dispatch} = useContext(ContextEditorState);
    return (
        <div className="flex flex-row">
            <h1> Boolean </h1>
            <InputBoolean onChange={(value) => {
                dispatch({type: "CONTROL_INPUT_BOOLEAN_UPDATED", id, value})
            }}/>
        </div>
    )
}

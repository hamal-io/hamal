import React, {FC, useContext, useState} from "react";
import {InputSelect, TextArea} from "./inputs";
import styles from "@/components/nodes/port.module.css";
import {PortInputWidget} from "@/components/nodes/port.tsx";
import {
    ControlIndex,
    ControlInputString,
    isControlInputBoolean,
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
        <div key={node.index} className={styles.wrapper} data-component="ports">
            {
                nodeControlIds[node.index].map(controlId => controls[controlId]).map((control) => {

                    if (isControlInputBoolean(control)) {
                        return <ControlInputBooleanWidget key={control.index} index={control.index}/>
                    }

                    if (isControlInputString(control)) {
                        return <ControlInputStringWidget key={control.index} control={control}/>
                    }

                    // throw `Not supported yet`
                })
            }
        </div>
    )
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
                dispatch({type: 'CONTROL_INPUT_STRING_UPDATED', index: control.index, value})
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
    index: ControlIndex
}

export const ControlInputBooleanWidget: FC<ControlInputBooleanWidgetProps> = ({index}) => {
    const {dispatch} = useContext(ContextEditorState);
    return (
        <div className="flex flex-row">
            <h1> Boolean </h1>
            <InputBoolean onChange={(value) => {
                dispatch({type: "CONTROL_INPUT_BOOLEAN_UPDATED", index, value})
            }}/>
        </div>
    )
}

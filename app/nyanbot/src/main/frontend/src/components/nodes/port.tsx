import React, {FC} from "react";
import styles from "@/components/nodes/port.module.css";

type PortsProps = {
    type: string;
}
export const Ports: FC<PortsProps> = ({}) => {
    return (
        <div className={styles.wrapper} data-nodes-component="ports">
            <div className={styles.inputs} data-nodes-component="ports-inputs">
                <PortInput/>
            </div>
            <div className={styles.outputs} data-nodes-component="ports-outputs">
                <PortOutput/>
            </div>
        </div>
    );
}


type PortInputProps = {}

export const PortInput: FC<PortInputProps> = ({}) => {
    return (
        <div
            data-nodes-component="port-input"
            className={styles.transput}
            // data-controlless={isConnected || noControls || !controls.length}
            onDragStart={e => {
                e.preventDefault();
                e.stopPropagation();
            }}
        >
            <Port
                // type={type}
                // color={color}
                // name={name}
                // nodeId={nodeId}
                // isInput
                // triggerRecalculation={triggerRecalculation}
            />
            {/*{(!controls.length || noControls || isConnected) && (*/}
            {/*    <label data-nodes-component="port-label" className={styles.portLabel}>*/}
            {/*        {label || defaultLabel}*/}
            {/*    </label>*/}
            {/*)}*/}
            {/*{!noControls && !isConnected ? (*/}
            {/*    <div className={styles.controls}>*/}
            {/*        {controls.map(control => (*/}
            {/*            <Control*/}
            {/*                {...control}*/}
            {/*                nodeId={nodeId}*/}
            {/*                portName={name}*/}
            {/*                triggerRecalculation={triggerRecalculation}*/}
            {/*                updateNodeConnections={updateNodeConnections}*/}
            {/*                inputLabel={label}*/}
            {/*                data={data[control.name]}*/}
            {/*                allData={data}*/}
            {/*                key={control.name}*/}
            {/*                inputData={inputData}*/}
            {/*                isMonoControl={controls.length === 1}*/}
            {/*            />*/}
            {/*        ))}*/}
            {/*    </div>*/}
            {/*) : null}*/}
        </div>
    )
}

type PortOutputProps = {}

export const PortOutput: FC<PortOutputProps> = ({}) => {
    return (
        <div
            data-nodes-component="port-output"
            className={styles.transput}
            data-controlless={true}
            onDragStart={e => {
                e.preventDefault();
                e.stopPropagation();
            }}
        >
            <label data-nodes-component="port-label" className={styles.portLabel}>
                {/*{label || defaultLabel}*/}
            </label>
            <Port
                // type={type}
                // name={name}
                // color={color}
                // nodeId={nodeId}
                // triggerRecalculation={triggerRecalculation}
            />
        </div>
    );
}

type PortProps = {}


export const Port: FC<PortProps> = ({}) => {
    const port = React.useRef<HTMLDivElement>(null);
    return (
        <React.Fragment>
            <div
                style={{zIndex: 999}}
                // onMouseDown={handleDragStart}
                className={styles.port}
                data-port-color={'pink'}
                // data-port-name={name}
                // data-port-type={type}
                // data-node-id={nodeId}
                data-nodes-component="port-handle"
                onDragStart={e => {
                    e.preventDefault();
                    e.stopPropagation();
                }}
                ref={port}
            />
            {/*{isDragging && !isInput ? (*/}
            {/*    <Portal*/}
            {/*        node={document.getElementById(`${DRAG_CONNECTION_ID}${editorId}`)}*/}
            {/*    >*/}
            {/*        <Connection*/}
            {/*            from={dragStartCoordinates}*/}
            {/*            to={dragStartCoordinates}*/}
            {/*            lineRef={line}*/}
            {/*        />*/}
            {/*    </Portal>*/}
            {/*) : null}*/}
        </React.Fragment>
    )
}
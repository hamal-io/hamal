import React, {FC} from "react";
import styles from "@/components/nodes/port.module.css";
import {Connection} from "@/components/nodes/connection.tsx";
import {Position} from "@/components/nodes/types.ts";

type PortsProps = {
    type: string;
}
export const Ports: FC<PortsProps> = ({}) => {
    return (
        <div className={styles.wrapper} data-component="ports">
            <div className={styles.inputs} data-component="ports-inputs">
                {/*<PortInput/>*/}
            </div>
            <div className={styles.outputs} data-component="ports-outputs">
                {/*<PortOutput/>*/}
            </div>
        </div>
    );
}


type PortInputProps = {}

export const PortInput: FC<PortInputProps> = ({}) => {
    return (
        <div
            data-component="port-input"
            className={styles.transput}
            // data-controlless={isConnected || noControls || !controls.length}
            onDragStart={e => {
                e.preventDefault();
                e.stopPropagation();
            }}
        >
            <Port
                isInput={true}
                // type={type}
                // color={color}
                // name={name}
                // nodeId={nodeId}
                // isInput
                // triggerRecalculation={triggerRecalculation}
            />
            {/*{(!controls.length || noControls || isConnected) && (*/}
            {/*    <label data-component="port-label" className={styles.portLabel}>*/}
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
            data-component="port-output"
            className={styles.transput}
            data-controlless={true}
            onDragStart={e => {
                e.preventDefault();
                e.stopPropagation();
            }}
        >
            <label data-component="port-label" className={styles.portLabel}>
                {/*{label || defaultLabel}*/}
            </label>
            <Port
                isInput={false}
                // type={type}
                // name={name}
                // color={color}
                // nodeId={nodeId}
                // triggerRecalculation={triggerRecalculation}
            />
        </div>
    );
}

type PortProps = {
    isInput: Boolean
}


export const Port: FC<PortProps> = ({
                                        isInput
                                    }) => {
    // const nodesDispatch = React.useContext(NodeDispatchContext);
    // const stageState = React.useContext(StageContext) || {
    //     scale: 1,
    //     translate: { x: 0, y: 0 }
    // };

    const stageState = {
        scale: 1,
        translate: {x: 0, y: 0}
    }

    // const editorId = React.useContext(EditorIdContext);
    // const stageId = `${STAGE_ID}${editorId}`;
    // const inputTypes = React.useContext(PortTypesContext) || {};
    const [isDragging, setIsDragging] = React.useState<Boolean>(false);
    const [dragStartPosition, setDragStartPosition] = React.useState<Position>({x: 0, y: 0});

    const dragStartCoordinatesCache = React.useRef(dragStartPosition);
    const port = React.useRef<HTMLDivElement>(null);

    const line = React.useRef<SVGPathElement>(null);
    const lineInToPort = React.useRef<HTMLDivElement | null>(null);

    // const byScale = (value: number) => (1 / (stageState?.scale ?? 1)) * value;
    const byScale = (value: number) => 1 * value;

    const calculatePath = (from: Position, to: Position): string => {
        return `M ${from.x} ${from.y} L ${to.x} ${to.y}`
    }

    const handleDrag = (e: MouseEvent) => {
        console.log('handleDrag')
        const {x, y, width, height} = document.getElementById('does-not-exists') // FIXME
            ?.getBoundingClientRect() ?? {x: 0, y: 0, width: 0, height: 0};

        if (isInput) {
            const to = {
                x:
                    byScale(e.clientX - x - width / 2) + byScale(stageState?.translate?.x ?? 1),
                y:
                    byScale(e.clientY - y - height / 2) + byScale(stageState?.translate?.y ?? 1)
            };


            lineInToPort.current?.setAttribute(
                "d",
                // calculateCurve(dragStartCoordinatesCache.current, to)
                calculatePath(dragStartCoordinatesCache.current, to)
            );
        } else {
            const to = {
                x:
                    byScale(e.clientX - x - width / 2) + byScale(stageState?.translate?.x ?? 1),
                y:
                    byScale(e.clientY - y - height / 2) + byScale(stageState?.translate?.y ?? 1)
            };

            line.current?.setAttribute(
                "d",
                calculatePath(dragStartCoordinatesCache.current, to)
            );
        }
    };

    const handleDragEnd = e => {
        const droppedOnPort = !!e.target.dataset.portName;

        if (isInput) {
            const {
                inputNodeId = "",
                inputPortName = "",
                outputNodeId = "",
                outputPortName = ""
            } = lineInToPort.current?.dataset ?? {};
            //FIXME
            // nodesDispatch?.({
            //     type: NodesActionType.REMOVE_CONNECTION,
            //     input: { nodeId: inputNodeId, portName: inputPortName },
            //     output: { nodeId: outputNodeId, portName: outputPortName }
            // });
            if (droppedOnPort) {
                const {
                    portName: connectToPortName,
                    nodeId: connectToNodeId,
                    portType: connectToPortType,
                    portTransputType: connectToTransputType
                } = e.target.dataset;
                const isNotSameNode = outputNodeId !== connectToNodeId;
                if (isNotSameNode && connectToTransputType !== "output") {
                    //FIXME
                    // const inputWillAcceptConnection = inputTypes[
                    //     connectToPortType
                    //     ].acceptTypes.includes(type);
                    // if (inputWillAcceptConnection) {
                    //     nodesDispatch?.({
                    //         type: NodesActionType.ADD_CONNECTION,
                    //         input: { nodeId: connectToNodeId, portName: connectToPortName },
                    //         output: { nodeId: outputNodeId, portName: outputPortName }
                    //     });
                    // }
                }
            }
        } else {
            if (droppedOnPort) {
                const {
                    portName: inputPortName,
                    nodeId: inputNodeId,
                    portType: inputNodeType,
                    portTransputType: inputTransputType
                } = e.target.dataset;
                //FIXME
                // const isNotSameNode = inputNodeId !== nodeId;
                // if (isNotSameNode && inputTransputType !== "output") {
                // const inputWillAcceptConnection = inputTypes[
                //     inputNodeType
                //     ].acceptTypes.includes(type);
                // if (inputWillAcceptConnection) {
                //     nodesDispatch?.({
                //         type: NodesActionType.ADD_CONNECTION,
                //         output: { nodeId, portName: name },
                //         input: { nodeId: inputNodeId, portName: inputPortName }
                //     });
                //     triggerRecalculation();
                // }
                // }
            }
        }
        setIsDragging(false);
        document.removeEventListener("mouseup", handleDragEnd);
        document.removeEventListener("mousemove", handleDrag);
    };

    const handleDragStart = e => {
        e.preventDefault();
        e.stopPropagation();

        console.log("port - drag start")

        const {
            x: startPortX = 0,
            y: startPortY = 0,
            width: startPortWidth = 0,
            height: startPortHeight = 0
        } = port.current?.getBoundingClientRect() || {};
//FIXME
        // const {
        //     x: stageX = 0,
        //     y: stageY = 0,
        //     width: stageWidth = 0,
        //     height: stageHeight = 0
        // } = document.getElementById(stageId)?.getBoundingClientRect() || {};

        const {
            x: stageX = 0,
            y: stageY = 0,
            width: stageWidth = 0,
            height: stageHeight = 0
        } = {}

        if (isInput) {
            // lineInToPort.current = document.querySelector(
            //     `[data-input-node-id="${nodeId}"][data-input-port-name="${name}"]`
            // );
            //FIXME
            lineInToPort.current = document.querySelector(
                `[data-input-node-id="1"][data-input-port-name="${name}"]`
            );


            const portIsConnected = !!lineInToPort.current;
            if (
                portIsConnected &&
                lineInToPort.current &&
                lineInToPort.current.parentElement
            ) {
                //FIXME
                lineInToPort.current.parentElement.style.zIndex = "9999";
                // const {
                //     x: outputPortX = 0,
                //     y: outputPortY = 0,
                //     width: outputPortWidth = 0,
                //     height: outputPortHeight = 0
                // } =
                // getPortRect(
                //     lineInToPort.current.dataset.outputNodeId || "",
                //     lineInToPort.current.dataset.outputPortName || "",
                //     "output"
                // ) || {};

                const {
                    x: outputPortX = 0,
                    y: outputPortY = 0,
                    width: outputPortWidth = 0,
                    height: outputPortHeight = 0
                } = {};

                const coordinates = {
                    x: byScale(outputPortX - stageX + outputPortWidth / 2 - stageWidth / 2) + byScale(stageState.translate.x),
                    y: byScale(outputPortY - stageY + outputPortWidth / 2 - stageHeight / 2) + byScale(stageState.translate.y)
                };
                setDragStartPosition(coordinates);
                dragStartCoordinatesCache.current = coordinates;
                setIsDragging(true);
                document.addEventListener("mouseup", handleDragEnd);
                document.addEventListener("mousemove", handleDrag);
            }
        } else {
            const coordinates = {
                x: byScale(startPortX - stageX + startPortWidth / 2 - stageWidth / 2) + byScale(stageState.translate.x),
                y: byScale(startPortY - stageY + startPortWidth / 2 - stageHeight / 2) + byScale(stageState.translate.y)
            };
            setDragStartPosition(coordinates);
            dragStartCoordinatesCache.current = coordinates;
            setIsDragging(true);
            document.addEventListener("mouseup", handleDragEnd);
            document.addEventListener("mousemove", handleDrag);
        }
    };

    return (
        <React.Fragment>
            {/*<div*/}
            {/*    style={{zIndex: 999}}*/}
            {/*    onMouseDown={handleDragStart}*/}
            {/*    className={styles.port}*/}
            {/*    // data-port-color={color}*/}
            {/*    // data-port-name={name}*/}
            {/*    // data-port-type={type}*/}
            {/*    data-port-transput-type={isInput ? "input" : "output"}*/}
            {/*    // data-node-id={nodeId}*/}
            {/*    data-component="port-handle"*/}
            {/*    onDragStart={e => {*/}
            {/*        e.preventDefault();*/}
            {/*        e.stopPropagation();*/}
            {/*    }}*/}
            {/*    ref={port}*/}
            {/*/>*/}
            {/*{isDragging && !isInput ? (*/}
            {/*    // <Portal*/}
            {/*    //     node={document.getElementById(`${DRAG_CONNECTION_ID}${editorId}`)}*/}
            {/*    // >*/}
            {/*    <Connection*/}
            {/*        from={dragStartPosition}*/}
            {/*        to={dragStartPosition}*/}
            {/*        lineRef={line}*/}
            {/*    />*/}
            {/*    // </Portal>*/}
            {/*) : null}*/}
        </React.Fragment>
    );
}
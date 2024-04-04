import React, {FC, useContext} from "react";
import styles from "@/components/nodes/port.module.css";
import {Connection} from "@/components/nodes/connection.tsx";
import {Position} from "@/components/nodes/types.ts";
import {ContextCanvasState} from "@/components/nodes/context.ts";

type PortsProps = {
    type: string;
}
export const Ports: FC<PortsProps> = ({}) => {
    return (
        <div className={styles.wrapper} data-component="ports">
            <div className={styles.inputs} data-component="ports-inputs">
                <PortInput/>
            </div>
            <div className={styles.outputs} data-component="ports-outputs">
                <PortOutput/>
            </div>
        </div>
    );
}


type PortInputProps = {}

export const PortInput: FC<PortInputProps> = ({}) => {
    return (
        <div
            data-component="port-input"
            className={styles.hook}
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
            className={styles.hook}
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


export const Port: FC<PortProps> = ({isInput}) => {
    // const nodesDispatch = React.useContext(NodeDispatchContext);
    // const stageState = React.useContext(StageContext) || {
    //     scale: 1,
    //     translate: { x: 0, y: 0 }
    // };

    const canvasState = useContext(ContextCanvasState)

    // const stageState = {
    //     scale: 1,
    //     translate: {x: 0, y: 0}
    // }

    // const editorId = React.useContext(EditorIdContext);
    // const stageId = `${STAGE_ID}${editorId}`;
    // const inputTypes = React.useContext(PortTypesContext) || {};
    const [isDragging, setIsDragging] = React.useState<Boolean>(false);
    const [startPosition, setStartPosition] = React.useState<Position>({x: 0, y: 0});

    // const startPosition = React.useRef(dragStartPosition);
    const port = React.useRef<HTMLDivElement>(null);

    const line = React.useRef<SVGPathElement>(null);
    const lineInToPort = React.useRef<HTMLDivElement | null>(null);

    const byScale = (value: number) => (1 / (canvasState.scale)) * value;

    const calculatePath = (from: Position, to: Position): string => {
        return `M ${from.x} ${from.y} L ${to.x} ${to.y}`
    }


    const handleDrag = (e: MouseEvent) => {
        // const {x, y, width, height} = document
        //     .getElementById(stageId)
        //     ?.getBoundingClientRect() ?? {x: 0, y: 0, width: 0, height: 0};

        const {x,y} = canvasState.position;
        const {width, height} = canvasState.size;

        // if (isInput) {
        //     const to = {
        //         x: byScale(e.clientX - x - width / 2) + byScale(canvasState.translate.x),
        //         y: byScale(e.clientY - y - height / 2) + byScale(canvasState.translate.y ?? 1)
        //     };
        //     lineInToPort.current?.setAttribute(
        //         "d",
        //         calculatePath(startPosition, to)
        //     );
        // } else {
        const to = {
            x: byScale(e.clientX - x - width / 2) + byScale(canvasState.translate.x),
            y: byScale(e.clientY - y - height / 2) + byScale(canvasState.translate.y)
        };

        console.log(startPosition, to)

        line.current?.setAttribute(
            "d",
            calculatePath(startPosition, to)
        );
        // }
    };

    // const handleDrag = (e: MouseEvent) => {
    //     console.log('handleDrag')
    //     // const {x, y, width, height} = document.getElementById('does-not-exists') // FIXME
    //     //     ?.getBoundingClientRect() ?? {x: 0, y: 0, width: 0, height: 0};
    //
    //     const {x, y} = canvasState.position;
    //     const {width, height} = canvasState.size;
    //
    //     if (isInput) {
    //         const to = {
    //             x: byScale(e.clientX - x - width / 2) + byScale(canvasState?.position?.x ?? 1),
    //             y: byScale(e.clientY - y - height / 2) + byScale(canvasState?.position?.y ?? 1)
    //         };
    //
    //
    //         lineInToPort.current?.setAttribute(
    //             "d",
    //             // calculateCurve(dragStartCoordinatesCache.current, to)
    //             calculatePath(startPosition, to)
    //         );
    //     } else {
    //         // const to = {
    //         //     x:
    //         //         byScale(e.clientX - x - width / 2) + byScale(canvasState?.position?.x ?? 1),
    //         //     y:
    //         //         byScale(e.clientY - y - height / 2) + byScale(canvasState?.position?.y ?? 1)
    //         // };
    //
    //         const to = {
    //             x: e.clientX,
    //             y: e.clientY
    //         }
    //
    //         console.log(startPosition, to)
    //
    //
    //         line.current?.setAttribute(
    //             "d",
    //             calculatePath(startPosition, to)
    //         );
    //     }
    // };

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
        const {
            x: startPortX = 0,
            y: startPortY = 0,
            width: startPortWidth = 0,
            height: startPortHeight = 0
        } = port.current?.getBoundingClientRect() || {};

        const stageX = canvasState.translate.x;
        const stageY = canvasState.translate.y;
        const stageWidth = canvasState.size.width;
        const stageHeight = canvasState.size.height;

        if (isInput) {
            // lineInToPort.current = document.querySelector(
            //     `[data-input-node-id="${nodeId}"][data-input-port-name="${name}"]`
            // );
            // const portIsConnected = !!lineInToPort.current;
            // if (
            //     portIsConnected &&
            //     lineInToPort.current &&
            //     lineInToPort.current.parentElement
            // ) {
            //     lineInToPort.current.parentElement.style.zIndex = "9999";
            //     const {
            //         x: outputPortX = 0,
            //         y: outputPortY = 0,
            //         width: outputPortWidth = 0,
            //         height: outputPortHeight = 0
            //     } =
            //     getPortRect(
            //         lineInToPort.current.dataset.outputNodeId || "",
            //         lineInToPort.current.dataset.outputPortName || "",
            //         "output"
            //     ) || {};
            //
            //     const coordinates = {
            //         x:
            //             byScale(
            //                 outputPortX - stageX + outputPortWidth / 2 - stageWidth / 2
            //             ) + byScale(stageState.translate.x),
            //         y:
            //             byScale(
            //                 outputPortY - stageY + outputPortWidth / 2 - stageHeight / 2
            //             ) + byScale(stageState.translate.y)
            //     };
            //     setDragStartCoordinates(coordinates);
            //     dragStartCoordinatesCache.current = coordinates;
            //     setIsDragging(true);
            //     document.addEventListener("mouseup", handleDragEnd);
            //     document.addEventListener("mousemove", handleDrag);
            // }
        } else {
            const coordinates = {
                x:
                    byScale(startPortX - stageX + startPortWidth / 2 - stageWidth / 2) +
                    byScale(canvasState.translate.x),
                y:
                    byScale(startPortY - stageY + startPortWidth / 2 - stageHeight / 2) +
                    byScale(canvasState.translate.y)
            };
            setStartPosition(coordinates);
            // dragStartCoordinatesCache.current = coordinates;
            setIsDragging(true);
            document.addEventListener("mouseup", handleDragEnd);
            document.addEventListener("mousemove", handleDrag);
        }
    };

    return (
        <React.Fragment>
            <div
                style={{zIndex: 999}}
                onMouseDown={handleDragStart}
                className={styles.port}
                // data-port-color={color}
                // data-port-name={name}
                // data-port-type={type}
                data-port-type={isInput ? "input" : "output"}
                // data-node-id={nodeId}
                data-component="port-handle"
                onDragStart={e => {
                    e.preventDefault();
                    e.stopPropagation();
                }}
                ref={port}
            />
            {/*{isDragging && !isInput ? (*/}
            {/*    // <Portal*/}
            {/*    //     node={document.getElementById(`${DRAG_CONNECTION_ID}${editorId}`)}*/}
            {/*    // >*/}
            <Connection
                from={startPosition}
                to={startPosition}
                lineRef={line}
            />
            {/*    // </Portal>*/}
            {/*) : null}*/}
        </React.Fragment>
    );
}
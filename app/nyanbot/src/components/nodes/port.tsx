import React, {FC, useContext} from "react";
import styles from "@/components/nodes/port.module.css";
import {PortIndex, Port, Position} from "@/components/nodes/types.ts";
import {ContextEditorState} from "@/components/nodes/editor.tsx";

type PortsProps = {}
export const Ports: FC<PortsProps> = ({}) => {
    return (
        <div className={styles.wrapper} data-component="ports">
            {/*<div className={styles.inputs} data-component="ports-inputs">*/}
            {/*    <PortInput/>*/}
            {/*</div>*/}
            {/*<div className={styles.outputs} data-component="ports-outputs">*/}
            {/*    <PortOutputWidget/>*/}
            {/*</div>*/}
        </div>
    );
}


type PortInputWidgetProps = {
    port: Port;
}

export const PortInputWidget: FC<PortInputWidgetProps> = ({port}) => {
    return (
        <div
            data-component="port-input"
            className={styles.bubble}
            // data-controlless={isConnected || noControls || !controls.length}
            onDragStart={e => {
                e.preventDefault();
                e.stopPropagation();
            }}
        >
            <PortWidget
                index={port.index}
                isInput={true}
                // type={type}
                // color={color}
                // name={name}
                // NodeIndex={NodeIndex}
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
            {/*                NodeIndex={NodeIndex}*/}
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

type PortOutputWidgetProps = {
    port: Port;
}

export const PortOutputWidget: FC<PortOutputWidgetProps> = ({port}) => {
    return (
        <div className={styles.wrapper} data-component="ports">
            <div className={styles.outputs} data-component="ports-outputs">
                <div
                    data-component="port-output"
                    className={styles.bubble}
                    data-controlless={true}
                    onDragStart={e => {
                        e.preventDefault();
                        e.stopPropagation();
                    }}
                >
                    <label data-component="port-label" className={styles.portLabel}>
                        {/*{label || defaultLabel}*/}
                    </label>
                    <PortWidget
                        index={port.index}
                        isInput={false}
                        // type={type}
                        // name={name}
                        // color={color}
                        // NodeIndex={NodeIndex}
                        // triggerRecalculation={triggerRecalculation}
                    />
                </div>
            </div>
        </div>

    );
}

type PortWidgetProps = {
    index: PortIndex;
    isInput: Boolean
}


export const PortWidget: FC<PortWidgetProps> = ({isInput, index}) => {
    // const nodesDispatch = React.useContext(NodeDispatchContext);
    // const stageState = React.useContext(StageContext) || {
    //     scale: 1,
    //     translate: { x: 0, y: 0 }
    // };

    const {state: {canvas}, dispatch} = useContext(ContextEditorState)

    // const stageState = {
    //     scale: 1,
    //     translate: {x: 0, y: 0}
    // }

    // const editorId = React.useContext(EditorIdContext);
    // const stageId = `${STAGE_ID}${editorId}`;
    // const inputTypes = React.useContext(PortTypesContext) || {};
    const [isDragging, setIsDragging] = React.useState<Boolean>(false);
    const [startPosition, setStartPosition] = React.useState<Position>({x: 0, y: 0});
    const startPositionCache = React.useRef(startPosition);


    // const startPosition = React.useRef(dragStartPosition);
    const port = React.useRef<HTMLDivElement>(null);


    const byScale = (value: number) => (1 / (canvas.scale)) * value;

    const calculatePath = (from: Position, to: Position): string => {
        return `M ${from.x} ${from.y} L ${to.x} ${to.y}`
    }


    const handleDrag = (e: MouseEvent) => {
        // const {x, y, width, height} = document
        //     .getElementById("CANVAS_1")
        //     ?.getBoundingClientRect() ?? {x: 0, y: 0, width: 0, height: 0};


        const {x, y} = canvas.position;
        // const x = 0; const y = 0;
        const {width, height} = canvas.size;

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
            x: byScale(e.clientX - x - width / 2) + byScale(canvas.translate.x),
            y: byScale(e.clientY - y - height / 2) + byScale(canvas.translate.y)
        };

        // line.current?.setAttribute(
        //     "d",
        //     calculatePath(startPositionCache.current, to)
        // );
        // }
        document.getElementById("TRANSIENT_CONNECTION").setAttribute("d", calculatePath(startPositionCache.current, to));
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
        console.log("drag end", e.target)
        console.log("drag end", e.target.dataset.portType)

        if (e.target.dataset.portType === 'input') {
            const outputPortIndex = index;
            const inputPortIndex = Number(e.target.dataset.portIndex)

            dispatch({type: "CONNECTION_ADDED", outputPortIndex, inputPortIndex})
        }


        // const droppedOnPort = !!e.target.dataset.portName;
        //
        // if (isInput) {
        //     const {
        //         inputNodeIndex = "",
        //         inputPortName = "",
        //         outputNodeIndex = "",
        //         outputPortName = ""
        //     } = lineInToPort.current?.dataset ?? {};
        //     //FIXME
        //     // nodesDispatch?.({
        //     //     type: NodesActionType.REMOVE_CONNECTION,
        //     //     inputs: { NodeIndex: inputNodeIndex, portName: inputPortName },
        //     //     output: { NodeIndex: outputNodeIndex, portName: outputPortName }
        //     // });
        //     if (droppedOnPort) {
        //         const {
        //             portName: connectToPortName,
        //             NodeIndex: connectToNodeIndex,
        //             portType: connectToPortType,
        //             portTransputType: connectToTransputType
        //         } = e.target.dataset;
        //         const isNotSameNode = outputNodeIndex !== connectToNodeIndex;
        //         if (isNotSameNode && connectToTransputType !== "output") {
        //             //FIXME
        //             // const inputWillAcceptConnection = inputTypes[
        //             //     connectToPortType
        //             //     ].acceptTypes.includes(type);
        //             // if (inputWillAcceptConnection) {
        //             //     nodesDispatch?.({
        //             //         type: NodesActionType.ADD_CONNECTION,
        //             //         inputs: { NodeIndex: connectToNodeIndex, portName: connectToPortName },
        //             //         output: { NodeIndex: outputNodeIndex, portName: outputPortName }
        //             //     });
        //             // }
        //         }
        //     }
        // } else {
        //     if (droppedOnPort) {
        //         const {
        //             portName: inputPortName,
        //             NodeIndex: inputNodeIndex,
        //             portType: inputNodeType,
        //             portTransputType: inputTransputType
        //         } = e.target.dataset;
        //         //FIXME
        //         // const isNotSameNode = inputNodeIndex !== NodeIndex;
        //         // if (isNotSameNode && inputTransputType !== "output") {
        //         // const inputWillAcceptConnection = inputTypes[
        //         //     inputNodeType
        //         //     ].acceptTypes.includes(type);
        //         // if (inputWillAcceptConnection) {
        //         //     nodesDispatch?.({
        //         //         type: NodesActionType.ADD_CONNECTION,
        //         //         output: { NodeIndex, portName: name },
        //         //         inputs: { NodeIndex: inputNodeIndex, portName: inputPortName }
        //         //     });
        //         //     triggerRecalculation();
        //         // }
        //         // }
        //     }
        // }

        document.getElementById("TRANSIENT_CONNECTION").setAttribute("d", `M 0,0 L 0, 0`);
        document.getElementById("TRANSIENT_CONNECTION").setAttribute("stroke-width", `0`);

        setIsDragging(false);
        document.removeEventListener("mouseup", handleDragEnd);
        document.removeEventListener("mousemove", handleDrag);
    };

    const handleDragStart = e => {
        e.preventDefault();
        e.stopPropagation();

        if (canvas.readonly) {
            return;
        }

        const {
            x: startPortX = 0,
            y: startPortY = 0,
            width: startPortWidth = 0,
            height: startPortHeight = 0
        } = port.current?.getBoundingClientRect() || {};

        const canvasX = canvas.position.x;
        const canvasY = canvas.position.y;
        const canvasWidth = canvas.size.width;
        const canvasHeight = canvas.size.height;

        if (isInput) {
            // lineInToPort.current = document.querySelector(
            //     `[data-inputs-node-id="${NodeIndex}"][data-inputs-port-name="${name}"]`
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
            //         lineInToPort.current.dataset.outputNodeIndex || "",
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
            const position = {
                x: byScale(startPortX - canvasX + startPortWidth / 2 - canvasWidth / 2) + byScale(canvas.translate.x),
                y: byScale(startPortY - canvasY + startPortHeight / 2 - canvasHeight / 2) + byScale(canvas.translate.y)
            };

            setStartPosition(position);
            startPositionCache.current = position;

            setIsDragging(true);
            document.addEventListener("mouseup", handleDragEnd);
            document.addEventListener("mousemove", handleDrag);
            document.getElementById("TRANSIENT_CONNECTION").setAttribute("d", `M ${position.x},${position.y} L ${position.x}, ${position.y}`);
            document.getElementById("TRANSIENT_CONNECTION").setAttribute("stroke-width", `3`);
        }
    };

    return (
        <>
            <div
                style={{zIndex: 999}}
                onMouseDown={handleDragStart}
                className={styles.port}
                // data-port-color={color}
                // data-port-name={name}
                // data-port-type={type}
                data-port-type={isInput ? "input" : "output"}
                // data-node-id={NodeIndex}
                data-port-index={index}
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
            {/*{isDragging && !isInput ? (*/}
            {/*<CanvasConnection*/}
            {/*    from={startPosition}*/}
            {/*    to={startPosition}*/}
            {/*    lineRef={line}*/}
            {/*/>*/}
            {/*) : null }*/}
            {/*    // </Portal>*/}
            {/*) : null}*/}
        </>
    );
}
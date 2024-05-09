import React, {useContext} from "react";
import styles from "./connection.module.css";
import {Connection, Position} from "@/components/nodes/types.ts";
import {getPortRect} from "@/components/nodes/calculate.ts";
import {ContextCanvasState} from "@/components/nodes/context.ts";

type ConnectionListWidgetProps = {
    connections: Connection[];
}

export const ConnectionListWidget = ({connections}: ConnectionListWidgetProps) => {
    const canvasState = useContext(ContextCanvasState)

    const byScale = (value: number) => (1 / (canvasState.scale)) * value;
    const {x, y} = canvasState.position;
    const {width, height} = canvasState.size;
    return (
        <svg
            id={"connection-canvas"}
            className={styles.wrapper}
            data-component="connection-canvas"
        >

            <path
                key={"TRANSIENT_CONNECTION"}
                id={"TRANSIENT_CONNECTION"}
                // data-connection-id={id}
                // data-output-node-id={outputNodeId}
                // data-output-port-name={outputPortName}
                // data-inputs-node-id={inputNodeId}
                // data-inputs-port-name={inputPortName}
                data-component="connection-path"
                stroke="rgb(185, 186, 189)"
                // fill="none"
                strokeWidth={3}
                strokeLinecap="round"
                // d={curve}
                d={`M 0,0 L 0,0`}
            />


            {connections.map((connection: Connection) => {
                    // console.log("Connection", connection);

                    const outputPortRect = getPortRect(connection.outputPort.id);
                    const inputPortRect = getPortRect(connection.inputPort.id);


                    const from = {
                        x: byScale((outputPortRect.x + outputPortRect.width / 2) - x - width / 2) + byScale(canvasState.translate.x),
                        y: byScale((outputPortRect.y + outputPortRect.height / 2) - y - height / 2) + byScale(canvasState.translate.y)
                    };


                    const to = {
                        x: byScale((inputPortRect.x + inputPortRect.width / 2) - x - width / 2) + byScale(canvasState.translate.x),
                        y: byScale((inputPortRect.y + inputPortRect.height / 2) - y - height / 2) + byScale(canvasState.translate.y)
                    };

                    const calculatePath = (from: Position, to: Position): string => {
                        return `M ${from.x} ${from.y} L ${to.x} ${to.y}`
                    }


                    return (
                        <path
                            key={connection.id}
                            data-connection-id={connection.id}
                            // data-output-node-id={outputNodeId}
                            // data-output-port-name={outputPortName}
                            // data-inputs-node-id={inputNodeId}
                            // data-inputs-port-name={inputPortName}
                            // data-component="connection-path"
                            stroke="rgb(185, 186, 189)"
                            // fill="none"
                            strokeWidth={3}
                            strokeLinecap="round"
                            // d={curve}
                            // d={`M -100,-300 L 20, -330`}
                            d={calculatePath(from, to)}
                        />
                    );
                }
            )}


        </svg>
    );
}

import React, {useContext} from "react";
import styles from "./connection.module.css";
import {Position} from "@/components/nodes/types.ts";
import {getPortRect} from "@/components/nodes/calculate.ts";
import {ContextEditorState} from "@/components/nodes/editor.tsx";

type ConnectionListWidgetProps = {}

export const ConnectionListWidget = ({}: ConnectionListWidgetProps) => {
    const {connections, canvas} = useContext(ContextEditorState).state

    const byScale = (value: number) => (1 / (canvas.scale)) * value;
    const {x, y} = canvas.position;
    const {width, height} = canvas.size;

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
                // data-output-node-id={outputNodeIndex}
                // data-output-port-name={outputPortName}
                // data-inputs-node-id={inputNodeIndex}
                // data-inputs-port-name={inputPortName}
                data-component="connection-path"
                stroke="rgb(185, 186, 189)"
                // fill="none"
                strokeWidth={0}
                strokeLinecap="round"
                // d={curve}
                d={`M 0,0 L 0,0`}
            />


            {Object.keys(connections).map((ConnectionIndex) => {
                    const connection = connections[ConnectionIndex];
                    // console.log("Connection", connection);

                    const outputPortRect = getPortRect(connection.outputPort.id);
                    const inputPortRect = getPortRect(connection.inputPort.id);


                    const from = {
                        x: byScale((outputPortRect.x + outputPortRect.width / 2) - x - width / 2) + byScale(canvas.translate.x),
                        y: byScale((outputPortRect.y + outputPortRect.height / 2) - y - height / 2) + byScale(canvas.translate.y)
                    };


                    const to = {
                        x: byScale((inputPortRect.x + inputPortRect.width / 2) - x - width / 2) + byScale(canvas.translate.x),
                        y: byScale((inputPortRect.y + inputPortRect.height / 2) - y - height / 2) + byScale(canvas.translate.y)
                    };

                    const calculatePath = (from: Position, to: Position): string => {
                        return `M ${from.x} ${from.y} L ${to.x} ${to.y}`
                    }


                    return (
                        <path
                            key={connection.id}
                            data-connection-id={connection.id}
                            // data-output-node-id={outputNodeIndex}
                            // data-output-port-name={outputPortName}
                            // data-inputs-node-id={inputNodeIndex}
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

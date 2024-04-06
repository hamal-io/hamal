import React from "react";
import styles from "./connection.module.css";

type ConnectionCanvasProps = {}

export const ConnectionCanvas = ({}: ConnectionCanvasProps) => {
    return (
        <svg id={"connection-canvas"} className={styles.wrapper} data-component="connection-canvas">
            <path
                id={"TRANSIENT_CONNECTION"}
                // data-connection-id={id}
                // data-output-node-id={outputNodeId}
                // data-output-port-name={outputPortName}
                // data-input-node-id={inputNodeId}
                // data-input-port-name={inputPortName}
                data-component="connection-path"
                stroke="rgb(185, 186, 189)"
                // fill="none"
                strokeWidth={3}
                strokeLinecap="round"
                // d={curve}
                d={`M 0,0 L 0,0`}
            />
        </svg>
    );
}

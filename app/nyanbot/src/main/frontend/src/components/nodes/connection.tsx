import React from "react";
import styles from "./connection.module.css";
import {Connection} from "@/components/nodes/types.ts";

type ConnectionListWidgetProps = {
    connections: Connection[];
}

export const ConnectionListWidget = ({connections}: ConnectionListWidgetProps) => {

    return (
        <svg id={"connection-canvas"} className={styles.wrapper} data-component="connection-canvas">


            {/*<path*/}
            {/*    id={"TRANSIENT_CONNECTION"}*/}
            {/*    // data-connection-id={id}*/}
            {/*    // data-output-node-id={outputNodeId}*/}
            {/*    // data-output-port-name={outputPortName}*/}
            {/*    // data-input-node-id={inputNodeId}*/}
            {/*    // data-input-port-name={inputPortName}*/}
            {/*    data-component="connection-path"*/}
            {/*    stroke="rgb(185, 186, 189)"*/}
            {/*    // fill="none"*/}
            {/*    strokeWidth={3}*/}
            {/*    strokeLinecap="round"*/}
            {/*    // d={curve}*/}
            {/*    d={`M 0,0 L 0,0`}*/}
            {/*/>*/}

            <path
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
                d={`M -300,275 L -130,50`}
            />

            <path
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
                d={`M 100,275 L 230,90`}
            />

            <path
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
                d={`M 450,275 L 570,150`}
            />

        </svg>
    );
}

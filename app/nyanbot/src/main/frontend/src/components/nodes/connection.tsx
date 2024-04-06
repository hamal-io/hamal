import React from "react";
import styles from "./connection.module.css";
import {Connection} from "@/components/nodes/types.ts";

type ConnectionCanvasProps = {
    connections: Connection[];
}

export const ConnectionCanvas = ({connections}: ConnectionCanvasProps) => {

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
                d={`M -200,75 L -90,25`}
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
                d={`M 150,75 L 260,30`}
            />


        </svg>
    );
}

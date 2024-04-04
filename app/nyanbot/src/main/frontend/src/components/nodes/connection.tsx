import React, {FC} from "react";
import styles from "@/components/nodes/connection.module.css";
import {Position} from "@/components/nodes/types.ts";

type ConnectionProps = {
    from: Position;
    to: Position;
    lineRef: React.Ref<SVGPathElement>;
}

export const Connection: FC<ConnectionProps> = ({
    from,
    to,
    lineRef
}) => {
    return (
        <svg className={styles.svg} data-component="connection-svg">

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
                d={`M ${from.x},${from.y} L ${to.x},${to.y}`}
                ref={lineRef}
            />
        </svg>
    )
}

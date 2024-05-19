import {PortIndex} from "@/components/nodes/types.ts";

export const getPort = (PortIndex: PortIndex) => document.querySelector(`[data-port-id="${PortIndex}"]`);

export const getPortRect = (PortIndex: PortIndex) => {
    const port = getPort(PortIndex);
    return port && port.getBoundingClientRect() || {
        x: 0,
        y: 0,
        width: 0,
        height: 0,
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    };
}




import {PortIndex} from "@/components/nodes/types.ts";

export const getPort = (index: PortIndex) => document.querySelector(`[data-port-index="${index}"]`);

export const getPortRect = (index: PortIndex) => {
    const port = getPort(index);
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




import {PortId} from "@/components/nodes/types.ts";

export const getPort = (portId: PortId) => document.querySelector(`[data-port-id="${portId}"]`);

export const getPortRect = (portId: PortId) => {
    const port = getPort(portId);
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




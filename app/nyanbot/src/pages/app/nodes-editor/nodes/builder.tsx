import NodesRegistry from "@/pages/app/nodes-editor/nodes/registry.ts";
import Point from "@/pages/app/nodes-editor/point.ts";
import {Node} from "reactflow";

export function buildNode(id: string, pos?: Point): Node {
    let node = NodesRegistry.find(node => node.id == id)
    const pr = randomCoordinateInSquare(new Point(100, 100), 500)
    if (pos == null) {
        node = {...node, position: {x: pr.x, y: pr.y}}
    }
    return node
}

function randomCoordinateInSquare(p: Point, len: number): Point {
    const x = Math.random() * len + p.x;
    const y = Math.random() * len + p.y;
    return new Point(x, y);
}
import {Action as ConnectionAction, initialState as connectionInitialState, reducer as connectionReducer, State as ConnectionState,} from './connection.ts'
import {Action as ControlAction, initialState as controlInitialState, reducer as controlReducer, State as ControlState} from './control.ts'
import {Action as NodeAction, initialState as nodeInitialState, reducer as nodeReducer, State as NodeState} from './node'
import {Dispatch, useCallback, useMemo, useReducer} from "react";
import {Connection, Control, Node} from "@/components/nodes/types.ts";

export type State = {
    connectionState: ConnectionState,
    controlState: ControlState,
    nodeState: NodeState
}

export type Action = ConnectionAction | ControlAction | NodeAction;

export const useState = (
    connections: Connection[],
    controls: Control[],
    nodes: Node[]
): [State, (action: Action) => void] => {
    const [connectionState, connectionDispatch] = useReducer(connectionReducer, connectionInitialState(connections))
    const [controlState, controlDispatch] = useReducer(controlReducer, controlInitialState(controls))
    const [nodeState, nodeDispatch] = useReducer(nodeReducer, nodeInitialState(nodes))

    const combinedDispatch = useCallback(combineDispatch(
        connectionDispatch,
        controlDispatch,
        nodeDispatch
    ), [connectionDispatch, controlDispatch, nodeDispatch]);

    const combinedState: State = useMemo(() => ({
        connectionState,
        controlState,
        nodeState
    }), [connectionState, controlState, nodeState]);

    return [combinedState, combinedDispatch]
}

const combineDispatch = (...dispatches: Dispatch<Action>[]) => (action: Action) =>
    dispatches.forEach((dispatch) => dispatch(action));
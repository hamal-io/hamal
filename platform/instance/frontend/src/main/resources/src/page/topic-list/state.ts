import {createContext} from "react";
import {ApiSimpleExecution, ApiSimpleFunction} from "../../api/types";
import {ApiSimpleTopic} from "../../api/event";

export const State = createContext([] as Array<ApiSimpleTopic>);


import {createContext} from "react";
import {ApiExecutionLog, ApiSimpleExecution, ApiSimpleFunction} from "../../api/types";

export const State = createContext([] as Array<ApiExecutionLog>);


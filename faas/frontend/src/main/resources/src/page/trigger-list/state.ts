import {createContext} from "react";
import {ApiSimpleExecution, ApiSimpleFunction} from "../../api/types";

export const State = createContext([] as Array<ApiSimpleFunction>);


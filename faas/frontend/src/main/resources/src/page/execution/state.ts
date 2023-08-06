import {createContext} from "react";
import {ApiSimpleExecution} from "../../api/types";

export const State = createContext([] as Array<ApiSimpleExecution>);


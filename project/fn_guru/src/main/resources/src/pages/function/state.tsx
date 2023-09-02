import {createContext} from "react";
import {ApiSimpleFunction} from "../../api/types";

export const State = createContext([] as Array<ApiSimpleFunction>);


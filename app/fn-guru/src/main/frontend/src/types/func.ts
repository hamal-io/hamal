export interface Func {
    id: string;
    name: string;
    inputs: object;
    code: {
        id: string;
        current: VersionedCode;
        deployed: VersionedCode;
    }
}

export interface FuncList {
    funcs: Array<FuncListItem>;
}

export interface FuncListItem {
    id: string;
    name: string;
}

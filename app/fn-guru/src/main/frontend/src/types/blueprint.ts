export interface Blueprint {
    id: string,
    name: string,
    inputs: object,
    value: string,
    description: string
}

export interface BlueprintCreateRequested {
    id: string;
    status: string;
    blueprintId: string;
}
export interface BlueprintList {
    blueprints: Array<BlueprintListItem>
}

export interface BlueprintListItem {
    id: string
    name: string
    description: string
}
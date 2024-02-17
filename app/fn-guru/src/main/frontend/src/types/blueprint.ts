export interface Blueprint {
    id: string,
    name: string,
    inputs: object,
    value: string
}

export interface BlueprintList {
    blueprints: Array<BlueprintListItem>
}

export interface BlueprintListItem {
    id: string
    name: string
    description: string
}
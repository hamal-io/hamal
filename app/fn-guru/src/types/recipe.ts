export interface Recipe {
    id: string,
    name: string,
    inputs: object,
    value: string,
    description: string
}

export interface RecipeCreateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}

export interface RecipeUpdateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}

export interface RecipeList {
    recipes: Array<RecipeListItem>
}

export interface RecipeListItem {
    id: string
    name: string
    description: string
}
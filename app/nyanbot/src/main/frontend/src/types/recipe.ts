import Element = React.JSX.Element;

export interface Tag {
    id: number
    name: string,
    icon: Element
}

export interface Recipe {
    id: string;
    name: string;
    description: string;
    tags: Array<Tag>
}

export interface TagList {
    ids: Array<number>
}
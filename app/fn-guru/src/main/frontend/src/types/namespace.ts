import {HotObject, HotObjectBuilder} from "@/types/HotObject.ts";


export interface NamespaceAppendRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    workspaceId: string;
}

export interface NamespaceUpdateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}

export type Features = HotObject<number>
export interface Namespace {
    id: string;
    name: string;
    features: Features;
}

export interface NamespaceList {
    namespaces: Array<NamespaceListItem>;
}

export interface NamespaceListItem {
    id: string;
    parentId: string;
    name: string;
}

export class NamespaceNode {
    data: NamespaceListItem
    descendants: Array<NamespaceNode>

    constructor(value: NamespaceListItem) {
        this.data = value
        this.descendants = []
    }

    addDescendant(other: NamespaceNode) {
        if (this.data.id !== other.data.id) {
            this.descendants.push(other)
        }
    }

    isParent() {
        return this.descendants.length > 0
    }
}

export class FeatureHotObject extends HotObjectBuilder<number> {

    constructor(features: Features = null) {
        super(features);
    }

    setFeature(key: string){
        super.set(key,0)
    }


    * [Symbol.iterator](): IterableIterator<string> {
        for (const [k, v] of Object.entries(super.get())) {
            yield k
        }
    }
}



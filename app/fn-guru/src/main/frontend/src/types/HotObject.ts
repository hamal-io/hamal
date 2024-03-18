export interface HotObject<T> {
    [key: string]: T
}

export class HotObjectBuilder<T> {
    private obj: HotObject<T>

    constructor(hotObject: HotObject<T> = null) {
        if (hotObject == null) {
            this.obj = {}
        } else {
            this.obj = {...hotObject}
            //Object.assign(this.obj, hotObject)
        }
    }

    set(key: string, value: T) {
        this.obj[key] = value
        return this
    }

    get() {
        return this.obj
    }

    delete(key: string) {
        delete this.obj[key]
    }

    toKeys() {
        const keys: string[] = []
        for (const [key,] of Object.entries(this.obj)) {
            keys.push(key)
        }
        return keys
    }

    toMap() {
        const map = new Map<string, T>
        for (const [k, v] of Object.entries(this.obj)) {
            map.set(k, v)
        }
        return map
    }
}


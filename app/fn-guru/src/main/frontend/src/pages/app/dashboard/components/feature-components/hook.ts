import {Namespace} from "@/types";
import {useCallback, useState} from "react";

type FetchAction = (namespace: Namespace) => void
type ToggleAction = (value: number) => void

export function useFeatures(): [FetchAction, ToggleAction, Array<Feature>] {
    const [current, setCurrent] = useState(currentFeatures)

    const fetch = useCallback<FetchAction>((namespace) => {
        const copy = [...current]
        for (const [k, v] of Object.entries(namespace.features)) {
            const x = copy.find((c) => v === c.value)
            x.state = true
        }
        setCurrent(copy)
    }, [])

    const toggle = useCallback((value: number) => {
        const copy = [...current]
        const x = copy.find((f) => f.value === value)
        x.toggle()
        setCurrent(copy)
    }, [])

    return [fetch, toggle, current]
}

export class Feature {
    name: string
    value: number
    state: boolean

    constructor(name: string, value: number) {
        this.name = name;
        this.value = value;
        this.state = false
    }

    toggle() {
        this.state = !this.state
    }
}

const currentFeatures = new Array<Feature>(
    new Feature("Schedule", 0),
    new Feature("Topic", 1),
    new Feature("Webhook", 2),
    new Feature("Endpoint", 3)
)


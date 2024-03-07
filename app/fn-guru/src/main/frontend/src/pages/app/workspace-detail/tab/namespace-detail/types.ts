interface NamespaceFeature {
    [key: string]: {
        value: number
        label: string
        description?: string
        icon?: Element
    }
}

interface NamespaceFeatureIterable extends NamespaceFeature {
    [Symbol.iterator]: () => Iterator<[string, NamespaceFeature[keyof NamespaceFeature]]>;
}

export const NamespaceFeatures: NamespaceFeatureIterable = {
    Schedules: {value: 0, label: "Schedules"},
    Topics: {value: 1, label: "Topics"},
    Webhooks: {value: 2, label: "Webhooks"},
    Endpoints: {value: 3, label: "Endpoints"},

    [Symbol.iterator]:
        function* () {
            for (const key of Object.keys(this)) {
                if (key !== Symbol.iterator) {
                    yield [key, this[key]];
                }
            }
        }
}



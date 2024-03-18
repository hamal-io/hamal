import {useState} from "react";

type MapType = (Map<string, boolean>)
type SetAction = (keys: string) => void
type ToggleAction = (key: string) => void
type GetAction = () => string[]
export const useFeature = (): [MapType, SetAction, ToggleAction, GetAction] => {
    const [map, setMap] = useState<Map<string, boolean>>(new Map<string, boolean>([
        ["Schedule", false],
        ["Topic", false],
        ["Webhook", false],
        ["Endpoint", false]
    ]));

    const setStates = (keys: string) => {
        const updatedMap = new Map(map);
        for (const s of keys.split(",")) {
            updatedMap.set(s, true);
        }
        setMap(updatedMap);
    };

    const toggleState = (key: string) => {
        const updatedMap = new Map(map);
        const state = map.get(key)
        updatedMap.set(key, !state);
        setMap(updatedMap);
    }

    const getActive = () => {
        const res = [];
        map.forEach((value, key) => {
            if (value) {
               res.push(key)
            }
        });
        return res;
    };

    return [map, setStates, toggleState, getActive]
}
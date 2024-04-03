import {Button} from "@/components/ui/button"
import Editor from "@/components/editor.tsx";
import React, {useState} from "react";
import {useAdhoc} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";

const AdhocPage = () => {
    return (<NodesAdhocPage/>)
}

export default AdhocPage;

const LuaAdhocPage = () => {
    const [uiState] = useUiState()
    const [adhoc, data] = useAdhoc()
    const [code, setCode] = useState("log = require('log').create({})\nlog.info('Let\\'s go..')")
    const Run = () => (
        <Button onClick={() => {
            adhoc(uiState.namespaceId, code, "Lua54")
        }}>Play</Button>
    )
    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Lua 5.4"
                    description="Have fun"
                    actions={[]}
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Run/>
                </div>
            </div>

            <div className="container h-full py-6">
                <div className="bg-white p-4 rounded-sm border-2">
                    <Editor code={code} onChange={setCode}/>
                </div>
            </div>
        </div>
    )
}

const NodesAdhocPage = () => {
    const [uiState] = useUiState()
    const [adhoc, data] = useAdhoc()
    const [code, setCode] = useState("{\n" +
        "  \"rootNode\": \"cbe10a17810000\",\n" +
        "  \"nodes\": [\n" +
        "    {\n" +
        "      \"id\": \"cbe10a17810000\",\n" +
        "      \"type\": \"LOAD_CONSTANT\",\n" +
        "      \"inputPortIds\": [],\n" +
        "      \"outputPortIds\": [\n" +
        "        \"cbe10a16410000\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18810000\",\n" +
        "      \"type\": \"FILTER_OBJECT\",\n" +
        "      \"inputPortIds\": [\n" +
        "        \"cbe10a18410000\"\n" +
        "      ],\n" +
        "      \"outputPortIds\": [\n" +
        "        \"cbe10a18410001\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18810002\",\n" +
        "      \"type\": \"PRINT\",\n" +
        "      \"inputPortIds\": [\n" +
        "        \"cbe10a18810001\"\n" +
        "      ],\n" +
        "      \"outputPortIds\": []\n" +
        "    }\n" +
        "  ],\n" +
        "  \"connections\": [\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18810003\",\n" +
        "      \"inputNodeId\": \"cbe10a18810000\",\n" +
        "      \"inputSlotId\": \"cbe10a18410000\",\n" +
        "      \"outputNodeId\": \"cbe10a17810000\",\n" +
        "      \"outputSlotId\": \"cbe10a16410000\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18810004\",\n" +
        "      \"inputNodeId\": \"cbe10a18810002\",\n" +
        "      \"inputSlotId\": \"cbe10a18810001\",\n" +
        "      \"outputNodeId\": \"cbe10a18810000\",\n" +
        "      \"outputSlotId\": \"cbe10a18410001\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"ports\": [\n" +
        "    {\n" +
        "      \"id\": \"cbe10a16410000\",\n" +
        "      \"name\": \"value\",\n" +
        "      \"type\": \"Output\",\n" +
        "      \"valueType\": \"TypeNumber\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18410000\",\n" +
        "      \"name\": \"input\",\n" +
        "      \"type\": \"Input\",\n" +
        "      \"valueType\": \"TypeNumber\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18410001\",\n" +
        "      \"name\": \"output\",\n" +
        "      \"type\": \"Output\",\n" +
        "      \"valueType\": \"TypeNumber\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"cbe10a18810001\",\n" +
        "      \"name\": \"number\",\n" +
        "      \"type\": \"Output\",\n" +
        "      \"valueType\": \"TypeNumber\"\n" +
        "    }\n" +
        "  ]\n" +
        "}")
    const Run = () => (
        <Button onClick={() => {
            adhoc(uiState.namespaceId, code, "Nodes")
        }}>Play</Button>
    )
    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Nodes"
                    description="Have fun"
                    actions={[]}
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Run/>
                </div>
            </div>

            <div className="container h-full py-6">
                <div className="bg-white p-4 rounded-sm border-2">
                    <Editor code={code} onChange={setCode}/>
                </div>
            </div>
        </div>
    )
}
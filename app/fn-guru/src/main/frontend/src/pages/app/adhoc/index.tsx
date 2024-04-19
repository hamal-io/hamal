import {Button} from "@/components/ui/button"
import Editor from "@/components/editor.tsx";
import React, {useState} from "react";
import {useAdhoc} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";

const AdhocPage = () => {
    return <LuaAdhocPage/>
    // return (<NodesAdhocPage/>)
}

export default AdhocPage;

const LuaAdhocPage = () => {
    const [uiState] = useUiState()
    const [adhoc, data] = useAdhoc()
    const [code, setCode] = useState("log = require('sys.log').create({})\nlog.info('Let\\'s go..')")
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
        "  \"nodes\": [\n" +
        "    {\n" +
        "      \"id\": \"1\",\n" +
        "      \"type\": \"Init\",\n" +
        "      \"title\": \"Init\",\n" +
        "      \"position\": {\n" +
        "        \"x\": -500,\n" +
        "        \"y\": 0\n" +
        "      },\n" +
        "      \"size\": {\n" +
        "        \"width\": 200,\n" +
        "        \"height\": 300\n" +
        "      },\n" +
        "      \"controls\": [],\n" +
        "      \"outputs\": [\n" +
        "        {\n" +
        "          \"id\": \"1\"\n" +
        "        }\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"2\",\n" +
        "      \"type\": \"Select\",\n" +
        "      \"title\": \"Select LP\",\n" +
        "      \"position\": {\n" +
        "        \"x\": -150,\n" +
        "        \"y\": 0\n" +
        "      },\n" +
        "      \"size\": {\n" +
        "        \"width\": 250,\n" +
        "        \"height\": 300\n" +
        "      },\n" +
        "      \"controls\": [\n" +
        "        {\n" +
        "          \"id\": \"1\",\n" +
        "          \"type\": \"Input\",\n" +
        "          \"ports\": [\n" +
        "            {\n" +
        "              \"id\": \"2\"\n" +
        "            }\n" +
        "          ]\n" +
        "        },\n" +
        "        {\n" +
        "          \"id\": \"2\",\n" +
        "          \"type\": \"Condition\",\n" +
        "          \"ports\": []\n" +
        "        }\n" +
        "      ],\n" +
        "      \"outputs\": [\n" +
        "        {\n" +
        "          \"id\": \"3\"\n" +
        "        }\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"3\",\n" +
        "      \"type\": \"ToText\",\n" +
        "      \"title\": \"LP to text\",\n" +
        "      \"position\": {\n" +
        "        \"x\": 200,\n" +
        "        \"y\": 0\n" +
        "      },\n" +
        "      \"size\": {\n" +
        "        \"width\": 250,\n" +
        "        \"height\": 300\n" +
        "      },\n" +
        "      \"controls\": [\n" +
        "        {\n" +
        "          \"id\": \"1\",\n" +
        "          \"type\": \"Input\",\n" +
        "          \"ports\": [\n" +
        "            {\n" +
        "              \"id\": \"4\"\n" +
        "            }\n" +
        "          ]\n" +
        "        },\n" +
        "        {\n" +
        "          \"id\": \"2\",\n" +
        "          \"type\": \"Text\",\n" +
        "          \"ports\": [],\n" +
        "          \"text\": \"{contract.address} has {total_holder}\",\n" +
        "          \"placeholder\": \"Turn into text\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"outputs\": [\n" +
        "        {\n" +
        "          \"id\": \"5\"\n" +
        "        }\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"4\",\n" +
        "      \"type\": \"TelegramMessageSend\",\n" +
        "      \"title\": \"Telegram send message\",\n" +
        "      \"position\": {\n" +
        "        \"x\": 550,\n" +
        "        \"y\": 0\n" +
        "      },\n" +
        "      \"size\": {\n" +
        "        \"width\": 250,\n" +
        "        \"height\": 300\n" +
        "      },\n" +
        "      \"controls\": [\n" +
        "        {\n" +
        "          \"id\": \"3\",\n" +
        "          \"type\": \"Text\",\n" +
        "          \"ports\": [],\n" +
        "          \"text\": \"\",\n" +
        "          \"placeholder\": \"chat_id\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"id\": \"4\",\n" +
        "          \"type\": \"Text\",\n" +
        "          \"ports\": [\n" +
        "            {\n" +
        "              \"id\": \"6\"\n" +
        "            }\n" +
        "          ],\n" +
        "          \"text\": \"\",\n" +
        "          \"placeholder\": \"message\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"outputs\": []\n" +
        "    }\n" +
        "  ],\n" +
        "  \"connections\": [\n" +
        "    {\n" +
        "      \"id\": \"1\",\n" +
        "      \"outputNode\": {\n" +
        "        \"id\": \"1\"\n" +
        "      },\n" +
        "      \"outputPort\": {\n" +
        "        \"id\": \"1\"\n" +
        "      },\n" +
        "      \"inputNode\": {\n" +
        "        \"id\": \"2\"\n" +
        "      },\n" +
        "      \"inputPort\": {\n" +
        "        \"id\": \"2\"\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"2\",\n" +
        "      \"outputNode\": {\n" +
        "        \"id\": \"2\"\n" +
        "      },\n" +
        "      \"outputPort\": {\n" +
        "        \"id\": \"3\"\n" +
        "      },\n" +
        "      \"inputNode\": {\n" +
        "        \"id\": \"3\"\n" +
        "      },\n" +
        "      \"inputPort\": {\n" +
        "        \"id\": \"4\"\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": \"3\",\n" +
        "      \"outputNode\": {\n" +
        "        \"id\": \"3\"\n" +
        "      },\n" +
        "      \"outputPort\": {\n" +
        "        \"id\": \"5\"\n" +
        "      },\n" +
        "      \"inputNode\": {\n" +
        "        \"id\": \"4\"\n" +
        "      },\n" +
        "      \"inputPort\": {\n" +
        "        \"id\": \"6\"\n" +
        "      }\n" +
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
import React from "react";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {useAuth} from "@/hooks/auth.ts";

export const TestPage = () => {
    const [auth] = useAuth()
    return (
        <ButtonPrimary onClick={() => {
            console.log("Test")

            fetch(`${import.meta.env.VITE_BASE_URL}/v1/flows`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({
                    name: 'dummy flow',
                    triggerType: 'web3::new_lp_found',
                    code: JSON.stringify(
                        {
                            "nodes": [
                                {
                                    "id": "1",
                                    "type": "Init",
                                    "title": "Init",
                                    "position": {
                                        "x": -500,
                                        "y": 0
                                    },
                                    "size": {
                                        "width": 200,
                                        "height": 300
                                    },
                                    "controls": [],
                                    "outputs": [
                                        {
                                            "id": "1"
                                        }
                                    ]
                                },
                                {
                                    "id": "2",
                                    "type": "Select",
                                    "title": "Select LP",
                                    "position": {
                                        "x": -150,
                                        "y": 0
                                    },
                                    "size": {
                                        "width": 250,
                                        "height": 300
                                    },
                                    "controls": [
                                        {
                                            "id": "1",
                                            "type": "Input",
                                            "ports": [
                                                {
                                                    "id": "2"
                                                }
                                            ]
                                        },
                                        {
                                            "id": "2",
                                            "type": "Condition",
                                            "ports": []
                                        }
                                    ],
                                    "outputs": [
                                        {
                                            "id": "3"
                                        }
                                    ]
                                },
                                {
                                    "id": "3",
                                    "type": "ToText",
                                    "title": "LP to text",
                                    "position": {
                                        "x": 200,
                                        "y": 0
                                    },
                                    "size": {
                                        "width": 250,
                                        "height": 300
                                    },
                                    "controls": [
                                        {
                                            "id": "1",
                                            "type": "Input",
                                            "ports": [
                                                {
                                                    "id": "4"
                                                }
                                            ]
                                        },
                                        {
                                            "id": "2",
                                            "type": "Text",
                                            "ports": [],
                                            "text": "{contract.address} has {total_holder}",
                                            "placeholder": "Turn into text"
                                        }
                                    ],
                                    "outputs": [
                                        {
                                            "id": "5"
                                        }
                                    ]
                                },
                                {
                                    "id": "4",
                                    "type": "TelegramMessageSend",
                                    "title": "Telegram send message",
                                    "position": {
                                        "x": 550,
                                        "y": 0
                                    },
                                    "size": {
                                        "width": 250,
                                        "height": 300
                                    },
                                    "controls": [
                                        {
                                            "id": "3",
                                            "type": "Text",
                                            "ports": [],
                                            "text": "",
                                            "placeholder": "chat_id"
                                        },
                                        {
                                            "id": "4",
                                            "type": "Text",
                                            "ports": [
                                                {
                                                    "id": "6"
                                                }
                                            ],
                                            "text": "",
                                            "placeholder": "message"
                                        }
                                    ],
                                    "outputs": []
                                }
                            ],
                            "connections": [
                                {
                                    "id": "1",
                                    "outputNode": {
                                        "id": "1"
                                    },
                                    "outputPort": {
                                        "id": "1"
                                    },
                                    "inputNode": {
                                        "id": "2"
                                    },
                                    "inputPort": {
                                        "id": "2"
                                    }
                                },
                                {
                                    "id": "2",
                                    "outputNode": {
                                        "id": "2"
                                    },
                                    "outputPort": {
                                        "id": "3"
                                    },
                                    "inputNode": {
                                        "id": "3"
                                    },
                                    "inputPort": {
                                        "id": "4"
                                    }
                                },
                                {
                                    "id": "3",
                                    "outputNode": {
                                        "id": "3"
                                    },
                                    "outputPort": {
                                        "id": "5"
                                    },
                                    "inputNode": {
                                        "id": "4"
                                    },
                                    "inputPort": {
                                        "id": "6"
                                    }
                                }
                            ]
                        }
                    )
                })
            })
                .then(response => {

                })
                .catch(error => {
                    console.error(error)
                })

        }}>
            Test
        </ButtonPrimary>
    )
}
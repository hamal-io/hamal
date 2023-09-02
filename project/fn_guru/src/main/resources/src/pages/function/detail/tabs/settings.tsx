import React, {useState} from "react";
import {ApiFunction} from "../../../../api/types";
import {Container, Form} from "@themesberg/react-bootstrap";

interface SettingsTabProps {
    func: ApiFunction
}

export default (props: SettingsTabProps) => {
    const [name, setName] = useState(props.func.name)
    return (
        <Form>
            <Form.Group className="mb-3">
                <Form.Label>Name</Form.Label>
                <Form.Control type="text" value={name}
                              onChange={evt => setName(evt.target.value)}
                              placeholder="Function name..."/>
            </Form.Group>
        </Form>
    )
}
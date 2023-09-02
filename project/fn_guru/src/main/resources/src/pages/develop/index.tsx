import React, {useState} from "react";
import {
    Breadcrumb,
    Button,
    Col,
    Row,
} from '@themesberg/react-bootstrap';
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Editor from "../../components/editor";

export default function () {
    const [code, setCode] = useState(`print("hello hamal")`)
    return (
        <>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                <div className="d-block mb-2 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item>Develop</Breadcrumb.Item></Breadcrumb>
                </div>
            </div>
            <Row>
                <Col xs={12} xl={8}>
                    {/*<Button onClick={_ => invokeAdhoc({code})} variant="primary" size="sm">Run Code</Button>*/}
                    <Editor
                        code={code}
                        onChange={code => setCode(code || '')}
                    />

                </Col>
                <Col xs={12} xl={4}>
                    <Row>
                        <Col xs={12}>
                            <h1>Inputs Placeholder</h1>
                            <h1>Events Placeholder</h1>
                            <h1>State Placeholder</h1>
                            <h1>Execution Logs Placeholder</h1>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </>
    );
};




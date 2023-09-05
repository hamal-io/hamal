import React, {useEffect, useState} from "react";
import {Breadcrumb, Button, Col, Form, Row} from '@themesberg/react-bootstrap';
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate, useParams} from "react-router-dom";
import {createFunction, getFunction, invokeAdhoc, listFunctions, updateFunction} from "../../api";
import {Simulate} from "react-dom/test-utils";
import Spinner from "../../component/spinner";
import {ApiFunction} from "../../api/types";
import Editor from "../../component/editor";


export default () => {
    const navigate = useNavigate()
    const {funcId} = useParams()
    const [func, setFunc] = useState<ApiFunction>({} as ApiFunction)

    const [name, setName] = useState()
    const [code, setCode] = useState('')
    const [loading, setLoading] = useState(false);


    const submit = () => {
        updateFunction(funcId!!, {name: name, code: code}).then(response => {
            navigate(`/functions/${response.id}`)
        })
    }

    useEffect(() => {
        if (funcId) {
            setLoading(true)
            getFunction(funcId).then(response => {
                setFunc((response))
                setCode(response.code.value)
                setName(response.name)
                setLoading(false)
            })
        }
    }, [funcId]);


    if (loading) {
        return (
            <>
                <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                    <div className="d-block mb-4 mb-md-0">
                        <Breadcrumb className="d-none d-md-inline-block"
                                    listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                            <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                            <Breadcrumb.Item onClick={() => navigate("/functions")}>Functions</Breadcrumb.Item>
                            <Breadcrumb.Item>{funcId}</Breadcrumb.Item>
                        </Breadcrumb>
                    </div>
                </div>
                <Spinner/>
            </>
        );
    }

    return (
        <>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item onClick={() => navigate("/functions")}>Functions</Breadcrumb.Item>
                        <Breadcrumb.Item>{func.name}</Breadcrumb.Item>
                    </Breadcrumb>
                </div>
            </div>
            <h1> Function Detail</h1>
            <Row>
                <Col xs={12} xl={8}>

                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Name</Form.Label>
                            <Form.Control type="text" value={name}
                                          onChange={evt => setName(evt.target.value)}
                                          placeholder="Function name..."/>
                        </Form.Group>
                    </Form>

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
                            <Button onClick={_ => submit()} variant="primary" size="sm">Update</Button>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </>
    );
};

import React, {useEffect, useState} from "react";
import {Breadcrumb, Button, Col, Form, Nav, Row, Tab} from '@themesberg/react-bootstrap';
import {faCode, faDatabase, faGear, faGears, faHome, faLaptopCode, faPalette} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate, useParams} from "react-router-dom";
import {getFunction, updateFunction} from "../../../api";
import Spinner from "../../../components/spinner";
import {ApiFunction} from "../../../api/types";
import Editor from "../../../components/editor";

import CodeTab from "./tabs/code";
import LogTab from './tabs/log'
import SettingsTab from './tabs/settings'
import TriggerTab from './tabs/trigger'


export default () => {
    const navigate = useNavigate()
    const {funcId} = useParams()
    const [func, setFunc] = useState<ApiFunction>({} as ApiFunction)

    const [name, setName] = useState('')
    const [code, setCode] = useState('')
    const [loading, setLoading] = useState(true);


    // const submit = () => {
    //     updateFunction(funcId!!, {name: name, code: code}).then(response => {
    //         navigate(`/functions/${response.id}`)
    //     })
    // }

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

            <Tab.Container defaultActiveKey="code_tab">
                <Row>
                    <Col lg={12}>
                        <Nav variant="pills" className="flex-column flex-sm-row">
                            <Nav.Item>
                                <Nav.Link eventKey="code_tab" className="mb-sm-3 mb-md-0">
                                    <FontAwesomeIcon icon={faCode} className="me-2"/> Code
                                </Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="trigger_tab" className="mb-sm-3 mb-md-0">
                                    <FontAwesomeIcon icon={faLaptopCode} className="me-2"/> Trigger ( 1 )
                                </Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="log_tab" className="mb-sm-3 mb-md-0">
                                    <FontAwesomeIcon icon={faDatabase} className="me-2"/> Log (1234567)
                                </Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="settings_tab" className="mb-sm-3 mb-md-0">
                                    <FontAwesomeIcon icon={faGear} className="me-2"/> Settings
                                </Nav.Link>
                            </Nav.Item>
                        </Nav>
                        <Tab.Content>
                            <Tab.Pane eventKey="code_tab" className="py-4">
                                <CodeTab func={func}/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="trigger_tab" className="py-4">
                                <TriggerTab/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="log_tab" className="py-4">
                                <LogTab/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="settings_tab" className="py-4">
                                <SettingsTab func={func}/>
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </>
    );
};

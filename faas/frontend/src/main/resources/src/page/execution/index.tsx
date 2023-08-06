import React, {useEffect, useState} from "react";
import {Breadcrumb, Col, Form, InputGroup, Row} from '@themesberg/react-bootstrap';
import {ExecutionTable} from "./table";
import {faHome, faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {listExecutions} from "../../api";

import {ApiSimpleExecution} from "../../api/types";
import {State} from "./state";


export default () => {
    const [executions, setExecutions] = useState([] as Array<ApiSimpleExecution>)
    useEffect(() => {
        listExecutions({limit: 1000}).then(response => {
            setExecutions(response.execs)
        })
    }, []);

    return (
        <State.Provider value={executions}>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item>Executions</Breadcrumb.Item></Breadcrumb>
                </div>
            </div>

            <div className="table-settings mb-4">
                <Row className="justify-content-between align-items-center">
                    <Col xs={8} md={6} lg={3} xl={4}>
                        <InputGroup>
                            <InputGroup.Text>
                                <FontAwesomeIcon icon={faSearch}/>
                            </InputGroup.Text>
                            <Form.Control type="text" placeholder="Search"/>
                        </InputGroup>
                    </Col>
                </Row>
            </div>

            <ExecutionTable/>
        </State.Provider>
    );
};




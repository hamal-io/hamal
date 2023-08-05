import React from "react";
import {Breadcrumb, Col, Form, InputGroup, Row} from '@themesberg/react-bootstrap';
import {ExecutionListTable} from "./ExecutionListTable";
import {faHome, faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export default () => {
    return (
        <>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-4">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item>Executions</Breadcrumb.Item></Breadcrumb>
                    <h4>Executions</h4>
                    <p className="mb-0">Your function executions.</p>
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

            <ExecutionListTable/>
        </>
    );
};




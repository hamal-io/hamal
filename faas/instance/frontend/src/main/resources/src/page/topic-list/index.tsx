import React, {useEffect, useState} from "react";
import {useNavigate} from 'react-router-dom';
import {Breadcrumb, Button, ButtonGroup, Col, Form, InputGroup, Row} from '@themesberg/react-bootstrap';
import {Modal} from '@themesberg/react-bootstrap';
import {TopicTable} from "./table";
import {faHome, faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {listTopics} from "../../api";

import {State} from "./state";
import {ApiSimpleTopic} from "../../api";


export default () => {
    const [topics, setTopics] = useState([] as Array<ApiSimpleTopic>)
    useEffect(() => {
        listTopics({limit: 1000}).then(response => {
            setTopics(response.topics)
        })
    }, []);

    const [showDefault, setShowDefault] = useState(false);
    const handleClose = () => setShowDefault(false);

    return (
        <State.Provider value={topics}>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item>Topics</Breadcrumb.Item></Breadcrumb>
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

            <TopicTable/>
        </State.Provider>
    );
};

import React, {useEffect, useState} from "react";
import {useNavigate} from 'react-router-dom';
import {Breadcrumb, Button, ButtonGroup, Col, Form, InputGroup, Row} from '@themesberg/react-bootstrap';
import {Modal} from '@themesberg/react-bootstrap';
import {ExecutionLogsTable} from "./table";
import {faHome, faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {listExecutionLogs,} from "../../api";

import {ApiExecutionLog} from "../../api/types";
import {State} from "./state";


export default () => {
    const [logs, setLogs] = useState([] as Array<ApiExecutionLog>)
    useEffect(() => {
        listExecutionLogs({limit: 1000}).then(response => {
            setLogs(response.logs)
        })
    }, []);

    const [showDefault, setShowDefault] = useState(false);
    const handleClose = () => setShowDefault(false);

    return (
        <State.Provider value={logs}>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item>Functions</Breadcrumb.Item></Breadcrumb>
                </div>
            </div>

            <ExecutionLogsTable/>
        </State.Provider>
    );
}
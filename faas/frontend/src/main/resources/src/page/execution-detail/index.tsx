import React, {useEffect, useState} from "react";
import {Breadcrumb} from '@themesberg/react-bootstrap';
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate, useParams} from "react-router-dom";
import {getExecution, getFunction, listFunctions} from "../../api";
import {Simulate} from "react-dom/test-utils";
import Spinner from "../../component/spinner";
import {ApiExecution, ApiFunction} from "../../api/types";


export default () => {
    const navigate = useNavigate()
    const {execId} = useParams()
    const [exec, setExec] = useState<ApiExecution>({} as ApiExecution)

    const [loading, setLoading] = useState(false);
    useEffect(() => {
        if (execId) {
            setLoading(true)
            getExecution(execId).then(response => {
                setExec((response))
                setLoading(false)
            })
        }
    }, [execId]);

    if (loading) {
        return (
            <>
                <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                    <div className="d-block mb-4 mb-md-0">
                        <Breadcrumb className="d-none d-md-inline-block"
                                    listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                            <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                            <Breadcrumb.Item onClick={() => navigate("/executions")}>Executions</Breadcrumb.Item>
                            <Breadcrumb.Item>{execId}</Breadcrumb.Item>
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
                        <Breadcrumb.Item onClick={() => navigate("/executions")}>Executions</Breadcrumb.Item>
                        <Breadcrumb.Item>{exec.id}</Breadcrumb.Item>
                    </Breadcrumb>
                </div>
            </div>
            <h1> Execution Detail</h1>
            {JSON.stringify(exec)}
        </>
    );
};

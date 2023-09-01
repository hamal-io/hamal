import React, {useEffect, useState} from "react";
import {Breadcrumb} from '@themesberg/react-bootstrap';
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate, useParams} from "react-router-dom";
import {getTrigger, listTriggers} from "../../api";
import {Simulate} from "react-dom/test-utils";
import Spinner from "../../component/spinner";
import {ApiTrigger} from "../../api/types";


export default () => {
    const navigate = useNavigate()
    const {funcId: triggerId} = useParams()
    const [trigger, setTrigger] = useState<ApiTrigger>({} as ApiTrigger)

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (triggerId) {
            setLoading(true)
            getTrigger(triggerId).then(response => {
                setTrigger((response))
                setLoading(false)
            })
        }
    }, [triggerId]);


    if (loading) {
        return (
            <>
                <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                    <div className="d-block mb-4 mb-md-0">
                        <Breadcrumb className="d-none d-md-inline-block"
                                    listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                            <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                            <Breadcrumb.Item onClick={() => navigate("/triggers")}>Triggers</Breadcrumb.Item>
                            <Breadcrumb.Item>{triggerId}</Breadcrumb.Item>
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
                        <Breadcrumb.Item onClick={() => navigate("/triggers")}>Triggers</Breadcrumb.Item>
                        <Breadcrumb.Item>{trigger.name}</Breadcrumb.Item>
                    </Breadcrumb>
                </div>
            </div>
            <h1> Trigger Detail</h1>
            {JSON.stringify(trigger)}
        </>
    );
};

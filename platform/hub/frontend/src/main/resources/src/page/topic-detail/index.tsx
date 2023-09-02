import React, {useEffect, useState} from "react";
import {Breadcrumb, Button, Col, Form, Row} from '@themesberg/react-bootstrap';
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate, useParams} from "react-router-dom";
import {ApiTopic, createFunction, getFunction, getTopic, invokeAdhoc, listFunctions, updateFunction} from "../../api";
import {Simulate} from "react-dom/test-utils";
import Spinner from "../../component/spinner";
import {ApiFunction} from "../../api/types";
import Editor from "../../component/editor";


export default () => {
    const navigate = useNavigate()
    const {topicId} = useParams()
    const [topic, setTopic] = useState<ApiTopic>({} as ApiTopic)
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (topicId) {
            setLoading(true)
            getTopic(topicId).then(response => {
                setTopic(response)
                setLoading(false)
            })
        }
    }, [topicId]);


    if (loading) {
        return (
            <>
                <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                    <div className="d-block mb-4 mb-md-0">
                        <Breadcrumb className="d-none d-md-inline-block"
                                    listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                            <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                            <Breadcrumb.Item onClick={() => navigate("/topics")}>Topics</Breadcrumb.Item>
                            <Breadcrumb.Item>{topicId}</Breadcrumb.Item>
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
                        <Breadcrumb.Item onClick={() => navigate("/topics")}>Topics</Breadcrumb.Item>
                        <Breadcrumb.Item>{topic.name}</Breadcrumb.Item>
                    </Breadcrumb>
                </div>
            </div>
            <h1> Topic Detail </h1>
        </>
    );
};

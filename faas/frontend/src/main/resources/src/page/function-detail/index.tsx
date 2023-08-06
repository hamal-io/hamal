import React from "react";
import {Breadcrumb} from '@themesberg/react-bootstrap';
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate, useParams} from "react-router-dom";


export default () => {
    const navigate = useNavigate()
    const {funcId} = useParams()
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
            <h1> Function Detail</h1>
        </>
    );
};

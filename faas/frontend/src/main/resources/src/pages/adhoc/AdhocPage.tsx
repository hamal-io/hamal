import React from "react";
import {Breadcrumb} from '@themesberg/react-bootstrap';

export default () => {
    return (
        <>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-4">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item>Adhoc</Breadcrumb.Item></Breadcrumb>
                    <h4>Adhoc</h4>
                </div>
            </div>
        </>
    );
};




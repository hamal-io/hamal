import {Spinner} from "@themesberg/react-bootstrap";
import React from "react";

export default () => (
    <Spinner animation="border" role="status">
        <span className="visually-hidden">Loading...</span>
    </Spinner>
)
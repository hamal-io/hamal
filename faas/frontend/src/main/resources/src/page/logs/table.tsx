import {Button, ButtonGroup, Card, Dropdown, Nav, Pagination, Table} from "@themesberg/react-bootstrap";
import {Link, useNavigate} from "react-router-dom";
import {Routes} from "../../routes";
import React from "react";

import {useContext} from 'react';
import {State} from './state';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faEllipsisH, faEye, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {ApiExecutionLog} from "../../api/types";


export const ExecutionLogsTable = () => {
    const logs = useContext(State)

    return (
        <Card border="light" className="table-wrapper table-responsive shadow-sm">
            <Card.Body className="pt-0">
                <Table hover className="user-table align-items-center">
                    <thead>
                    <tr>
                        <th className="border-bottom">Level</th>
                        <th className="border-bottom">ExecId</th>
                        <th className="border-bottom">Local At</th>
                        <th className="border-bottom">Remote At</th>
                        <th className="border-bottom">Message</th>
                    </tr>
                    </thead>
                    <tbody>
                    {logs.map(t => <TableRow key={`execution-${t.id}`} {...t} />)}
                    </tbody>
                </Table>
                {/*<Card.Footer className="px-3 border-0 d-lg-flex align-items-center justify-content-between">*/}
                {/*    <Nav>*/}
                {/*        <Pagination className="mb-2 mb-lg-0">*/}
                {/*            <Pagination.Prev>*/}
                {/*                Previous*/}
                {/*            </Pagination.Prev>*/}
                {/*            <Pagination.Item active>1</Pagination.Item>*/}
                {/*            <Pagination.Item>2</Pagination.Item>*/}
                {/*            <Pagination.Item>3</Pagination.Item>*/}
                {/*            <Pagination.Item>4</Pagination.Item>*/}
                {/*            <Pagination.Item>5</Pagination.Item>*/}
                {/*            <Pagination.Next>*/}
                {/*                Next*/}
                {/*            </Pagination.Next>*/}
                {/*        </Pagination>*/}
                {/*    </Nav>*/}
                {/*</Card.Footer>*/}
            </Card.Body>
        </Card>
    );
};


const TableRow = (props: ApiExecutionLog) => {
    const navigate = useNavigate()

    const {level, execId, message, localAt, remoteAt} = props;

    return (
        <tr>
            <td>
          <span className="fw-normal">
            {level}
          </span>
            </td>
            <td>
                <Card.Link onClick={() => navigate(`/execs/${execId}`)} className="fw-normal">
                    {execId}
                </Card.Link>
            </td>
            <td>
          <span className="fw-normal">
            {localAt}
          </span>
            </td>
            <td>
          <span className="fw-normal">
            {remoteAt}
          </span>
            </td>
            <td>
          <span className="fw-normal">
            {message}
          </span>
            </td>
        </tr>
    );
};

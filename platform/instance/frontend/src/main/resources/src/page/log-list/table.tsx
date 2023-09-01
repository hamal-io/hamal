import {Card, Table} from "@themesberg/react-bootstrap";
import {useNavigate} from "react-router-dom";
import React from "react";

import {useContext} from 'react';
import {State} from './state';
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

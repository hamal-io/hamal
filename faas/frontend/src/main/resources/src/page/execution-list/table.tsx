import moment from "moment-timezone";
import {Button, ButtonGroup, Card, Dropdown, Nav, Pagination, Table} from "@themesberg/react-bootstrap";
import {Link} from "react-router-dom";
import {Routes} from "../../routes";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";

import {useContext} from 'react';
import {State} from './state';


export const ExecutionTable = () => {
    const executions = useContext(State)
    const totalExecutions = executions.length;

    return (
        <Card border="light" className="table-wrapper table-responsive shadow-sm">
            <Card.Body className="pt-0">
                <Table hover className="user-table align-items-center">
                    <thead>
                    <tr>
                        <th className="border-bottom">#</th>
                        <th className="border-bottom">Function</th>
                        <th className="border-bottom">Correlation</th>
                        <th className="border-bottom">Last Update At</th>
                        <th className="border-bottom">Status</th>
                        <th className="border-bottom">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {executions.map(t => <TableRow key={`execution-${t.id}`} {...t} />)}
                    </tbody>
                </Table>
                <Card.Footer className="px-3 border-0 d-lg-flex align-items-center justify-content-between">
                    <Nav>
                        <Pagination className="mb-2 mb-lg-0">
                            <Pagination.Prev>
                                Previous
                            </Pagination.Prev>
                            <Pagination.Item active>1</Pagination.Item>
                            <Pagination.Item>2</Pagination.Item>
                            <Pagination.Item>3</Pagination.Item>
                            <Pagination.Item>4</Pagination.Item>
                            <Pagination.Item>5</Pagination.Item>
                            <Pagination.Next>
                                Next
                            </Pagination.Next>
                        </Pagination>
                    </Nav>
                    <small className="fw-bold">
                        Showing <b>{totalExecutions}</b> out of <b>25</b> entries
                    </small>
                </Card.Footer>
            </Card.Body>
        </Card>
    );
};


const TableRow = (props: any) => {
    const {id, func, correlationId, lastUpdatedAt, dueDate, status} = props;
    const statusVariant = status === "Completed" ? "success" : status === "Canceled" ? "warning" : status === "Failed" ? "danger" : "primary";

    return (
        <tr>
            <td>
                <Card.Link as={Link} to={Routes.Dashboard.path} className="fw-normal">
                    {id}
                </Card.Link>
            </td>
            <td>
          <span className="fw-normal">
            {func || 'adhoc invocation'}
          </span>
            </td>

            <td>
          <span className="fw-normal">
              {correlationId || func == null ? "N/A" : ""}
          </span>
            </td>

            <td>
          <span className="fw-normal">
            {lastUpdatedAt}
          </span>
            </td>
            <td>
          <span className={`fw-normal text-${statusVariant}`}>
            {status}
          </span>
            </td>
            <td>
                {/*      <Dropdown as={ButtonGroup}>*/}
                {/*          <Dropdown.Toggle as={Button} split variant="link" className="text-dark m-0 p-0">*/}
                {/*<span className="icon icon-sm">*/}
                {/*  <FontAwesomeIcon icon={faEllipsisH} className="icon-dark"/>*/}
                {/*</span>*/}
                {/*          </Dropdown.Toggle>*/}
                {/*          <Dropdown.Menu>*/}
                {/*              <Dropdown.Item>*/}
                {/*                  <FontAwesomeIcon className="me-2"/> View Details*/}
                {/*              </Dropdown.Item>*/}
                {/*              <Dropdown.Item>*/}
                {/*                  <FontAwesomeIcon icon={faEdit} className="me-2"/> Edit*/}
                {/*              </Dropdown.Item>*/}
                {/*              <Dropdown.Item className="text-danger">*/}
                {/*                  <FontAwesomeIcon icon={faTrashAlt} className="me-2"/> Remove*/}
                {/*              </Dropdown.Item>*/}
                {/*          </Dropdown.Menu>*/}
                {/*      </Dropdown>*/}
            </td>
        </tr>
    );
};

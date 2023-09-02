import {Button, ButtonGroup, Card, Dropdown, Nav, Pagination, Table} from "@themesberg/react-bootstrap";
import {Link, useNavigate} from "react-router-dom";
import {Routes} from "../../routes";
import React from "react";

import {useContext} from 'react';
import {State} from './state';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faEllipsisH, faEye, faTrashAlt} from "@fortawesome/free-solid-svg-icons";


export const ExecutionTable = () => {
    const functions = useContext(State)
    const totalFunctions = functions.length;

    return (
        <Card border="light" className="table-wrapper table-responsive shadow-sm">
            <Card.Body className="pt-0">
                <Table hover className="user-table align-items-center">
                    <thead>
                    <tr>
                        <th className="border-bottom">#</th>
                        <th className="border-bottom">Name</th>
                        <th className="border-bottom">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {functions.map(t => <TableRow key={`execution-${t.id}`} {...t} />)}
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
                        Showing <b>{totalFunctions}</b> out of <b>25</b> entries
                    </small>
                </Card.Footer>
            </Card.Body>
        </Card>
    );
};


const TableRow = (props: any) => {
    const navigate = useNavigate()

    const {id, name} = props;
    const statusVariant = status === "Completed" ? "success" : status === "Canceled" ? "warning" : status === "Failed" ? "danger" : "primary";

    return (
        <tr>
            <td>
                <Card.Link onClick={() => navigate(`/functions/${id}`)} className="fw-normal">
                    {id}
                </Card.Link>
            </td>
            <td>
          <span className="fw-normal">
            {name}
          </span>
            </td>
            <td>
                <Dropdown as={ButtonGroup}>
                    <Dropdown.Toggle as={Button} split variant="link" className="text-dark m-0 p-0">
                <span className="icon icon-sm">
                  <FontAwesomeIcon icon={faEllipsisH} className="icon-dark"/>
                </span>
                    </Dropdown.Toggle>
                    <Dropdown.Menu>
                        <Dropdown.Item>
                            <FontAwesomeIcon icon={faEye} className="me-2"/> View Details
                        </Dropdown.Item>
                        <Dropdown.Item>
                            <FontAwesomeIcon icon={faEdit} className="me-2"/> Edit
                        </Dropdown.Item>
                        <Dropdown.Item className="text-danger">
                            <FontAwesomeIcon icon={faTrashAlt} className="me-2"/> Remove
                        </Dropdown.Item>
                    </Dropdown.Menu>
                </Dropdown>
            </td>
        </tr>
    );
};

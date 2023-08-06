import moment from "moment-timezone";
import {Button, ButtonGroup, Card, Dropdown, Nav, Pagination, Table} from "@themesberg/react-bootstrap";
import {Link} from "react-router-dom";
import {Routes} from "../../routes";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";

const transactions = [
    {
        "id": 300500,
        "status": "Completed",
        "func": "Platinum function Plan",
        "lastUpdatedAt": moment().subtract(1, "days").format("DD MMM YYYY"),
        "dueDate": moment().subtract(1, "days").add(1, "month").format("DD MMM YYYY")
    },
    {
        "id": 300499,
        "status": "Completed",
        "func": "Platinum function Plan",
        "lastUpdatedAt": moment().subtract(2, "days").format("DD MMM YYYY"),
        "dueDate": moment().subtract(2, "days").add(1, "month").format("DD MMM YYYY")
    },
    {
        "id": 300498,
        "status": "Completed",
        "func": "Platinum function Plan",
        "correlationId": "test",
        "issulastUpdatedAteDate": moment().subtract(2, "days").format("DD MMM YYYY"),
        "dueDate": moment().subtract(2, "days").add(1, "month").format("DD MMM YYYY")
    },
    {
        "id": 300497,
        "status": "Completed",
        "func": "Flexible function Plan",
        "issueDate": moment().subtract(3, "days").format("DD MMM YYYY"),
        "dueDate": moment().subtract(3, "days").add(1, "month").format("DD MMM YYYY")
    },
    {
        "id": 300496,
        "status": "Failed",
        "func": "Gold function Plan",
        "correlationId": "test",
        "lastUpdatedAt": moment().subtract(1, "day").subtract(1, "month").format("DD MMM YYYY"),
        "dueDate": moment().subtract(1, "day").format("DD MMM YYYY")
    },
    {
        "id": 300495,
        "status": "Failed",
        "func": "Gold function Plan",
        "correlationId": "test",
        "lastUpdatedAt": moment().subtract(3, "days").subtract(1, "month").format("DD MMM YYYY"),
        "dueDate": moment().subtract(3, "days").format("DD MMM YYYY")
    },
    {
        "id": 300494,
        "status": "In Progress",
        "func": "Flexible function Plan",
        "correlationId": "test",
        "lastUpdatedAt": moment().subtract(4, "days").subtract(1, "month").format("DD MMM YYYY"),
        "dueDate": moment().subtract(4, "days").format("DD MMM YYYY")
    },
    {
        "id": 300493,
        "status": "Canceled",
        "func": "Gold function Plan",
        "correlationId": "test",
        "lastUpdatedAt": moment().subtract(20, "days").subtract(1, "month").format("DD MMM YYYY"),
        "dueDate": moment().subtract(20, "days").format("DD MMM YYYY")
    },
    {
        "id": 300492,
        "status": "Canceled",
        "func": "Platinum function Plan",
        "correlationId": "test",
        "lastUpdatedAt": moment().subtract(2, "months").format("DD MMM YYYY"),
        "dueDate": moment().subtract(3, "months").format("DD MMM YYYY")
    },
    {
        "id": 300491,
        "status": "Completed",
        "func": "Platinum function Plan",
        "correlationId": "test",
        "lastUpdatedAt": moment().subtract(6, "days").format("DD MMM YYYY"),
        "dueDate": moment().subtract(6, "days").add(1, "month").format("DD MMM YYYY")
    }
]


export const ExecutionTable = () => {

    const totalTransactions = transactions.length;

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
            {func}
          </span>
                </td>

                <td>
          <span className="fw-normal">
              {correlationId}
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
                    {transactions.map(t => <TableRow key={`execution-${t.id}`} {...t} />)}
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
                        Showing <b>{totalTransactions}</b> out of <b>25</b> entries
                    </small>
                </Card.Footer>
            </Card.Body>
        </Card>
    );
};

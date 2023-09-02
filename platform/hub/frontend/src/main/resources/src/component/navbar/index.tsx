import React from "react";
import {Container, Navbar} from '@themesberg/react-bootstrap';
import {Nav,} from '@themesberg/react-bootstrap';


export default (props: any) => {
    return (
        <Navbar className="bg-body-tertiary">
            <Container>
                <Nav className="me-auto">
                    <Nav.Item>
                        <Nav.Link href="/">Dashboard</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/adhoc">Adhoc</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/executions">Executions</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/logs">Logs</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/functions">Functions</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/triggers">Triggers</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/topics">Topics</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/settings">Settings</Nav.Link>
                    </Nav.Item>
                </Nav>
            </Container>
        </Navbar>
    );
};

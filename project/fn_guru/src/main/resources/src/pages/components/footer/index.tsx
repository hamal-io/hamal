import React from "react";
import moment from "moment-timezone";
import {Row, Col, Card, Container, Navbar} from '@themesberg/react-bootstrap';


interface FooterProps {
}

export default (props: FooterProps) => {
    const currentYear = moment().get("year");
    return (
        <footer className="footer bg-dark text-white">
            <Container>
                <Row>
                    <Col md={4}>
                        <Navbar.Brand className="me-lg-3 mb-3 d-flex align-items-center">
                            <span className="ms-2 brand-text">fn(guru)</span>
                        </Navbar.Brand>
                        <p>Develop as Fast as You Can Think.</p>
                    </Col>
                </Row>
                <hr className="bg-gray my-5"/>
                <Row>
                    <Col className="mb-md-2">
                        <Card.Link href="https://themesberg.com" target="_blank"
                                   className="d-flex justify-content-center">
                        </Card.Link>
                        <div className="d-flex text-center justify-content-center align-items-center"
                             role="contentinfo">
                            <p className="font-weight-normal font-small mb-0">Copyright © hamal.io {currentYear}. All
                                rights
                                reserved.</p>
                        </div>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

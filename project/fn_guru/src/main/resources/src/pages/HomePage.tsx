import React from "react";
import {useNavigate} from 'react-router-dom';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSignIn} from "@fortawesome/free-solid-svg-icons";
import {Button, Col, Container, Nav, Navbar, Row} from '@themesberg/react-bootstrap';
import Footer from "./components/footer";
import Editor from "../components/editor";

export default () => {
    const navigate = useNavigate()
    return (
        <>
            <Navbar variant="dark" expand="lg" bg="dark" className="navbar-transparent navbar-theme-primary sticky-top">
                <Container className="position-relative justify-content-between px-3">
                    <Navbar.Brand className="me-lg-3 d-flex align-items-center">
                        <span className="ms-2 brand-text d-none d-md-inline">fn(guru)</span>
                    </Navbar.Brand>

                    <div className="d-flex align-items-center">
                        <Navbar.Collapse>
                            <Nav className="navbar-nav-hover align-items-lg-center">
                                <Nav.Link className={"link-light"}>Features</Nav.Link>
                                <Nav.Link className={"link-light"}>Pricing</Nav.Link>
                                <Nav.Link className={"link-light"}>Documentation</Nav.Link>
                            </Nav>
                        </Navbar.Collapse>

                        <Button variant="outline-white" className="ms-3" onClick={evt => navigate(`/sign-in`)}>
                            <FontAwesomeIcon icon={faSignIn} className="me-1"/> Sign In
                        </Button>
                    </div>
                </Container>
            </Navbar>
            <section className="section-header overflow-hidden pt-5 pt-lg-6 pb-9 pb-lg-12 bg-primary text-white"
                     id="home">
                <Container>
                    <Row>
                        <Col xs={12} className="text-center">
                            <div className="react-big-icon d-none d-lg-block"><span className="fab fa-react"></span>
                            </div>
                            <h1 className="fw-bolder text-secondary">Simplest way to create Cloud Functions</h1>
                            <p className="text-muted fw-light mb-5 h5">Open source, no installation, no command-line
                                tools.
                                Write code and deploy functions from the browser.</p>
                        </Col>
                    </Row>
                    <figure className="position-absolute bottom-0 left-0 w-100 d-none d-md-block mb-n2">
                        <svg className="fill-soft" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 3000 185.4">
                            <path d="M3000,0v185.4H0V0c496.4,115.6,996.4,173.4,1500,173.4S2503.6,115.6,3000,0z"/>
                        </svg>
                    </figure>
                </Container>
            </section>
            <section className="section pt-0">
                <Container className="mt-n10 mt-lg-n12 z-2">
                    <Row className="justify-content-center">
                        <Col xs={12}>
                            <Editor
                                code={`local log = require('log')\nlog.info("That wasn't hard, was it?")`}
                                onChange={code => console.log("run me", code)}
                            />
                        </Col>
                    </Row>
                    <Row className="justify-content-center">
                        <Col xs={12}>
                            <Button variant="white" className="m-1">Test</Button>
                            <Button variant="primary" className="m-1">Deploy</Button>
                        </Col>
                    </Row>
                </Container>
            </section>
            <Footer/>
        </>
    );
};

import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faDownload,
    faExternalLinkAlt,
    faPager
} from "@fortawesome/free-solid-svg-icons";
import {faBootstrap, faReact, faSass} from "@fortawesome/free-brands-svg-icons";
import {Badge, Button, Card, Col, Container, Image, Nav, Navbar, Row} from '@themesberg/react-bootstrap';
import MockupPresentation from "../assets/img/mockup-presentation.png";
import ReactHero from "../assets/img/technologies/react-hero-logo.svg";
import Footer from "./components/footer";
import GitHubButton from "react-github-btn";

export default () => {

    return (
        <>
            <Navbar variant="dark" expand="lg" bg="dark" className="navbar-transparent navbar-theme-primary sticky-top">
                <Container className="position-relative justify-content-between px-3">
                    <Navbar.Brand className="me-lg-3 d-flex align-items-center">
                        <Image src={ReactHero}/>
                        <span className="ms-2 brand-text d-none d-md-inline">Volt React</span>
                    </Navbar.Brand>

                    <div className="d-flex align-items-center">
                        <Navbar.Collapse id="navbar-default-primary">
                            <Nav className="navbar-nav-hover align-items-lg-center">
                                <Nav.Link>Features</Nav.Link>
                                <Nav.Link>Pages</Nav.Link>
                                <Nav.Link className="d-sm-none d-xl-inline">Folder Structure</Nav.Link>
                                <Nav.Link>Getting Started</Nav.Link>
                                <Nav.Link>Upgrade to Pro</Nav.Link>
                            </Nav>
                        </Navbar.Collapse>
                        <Button variant="outline-white" className="ms-3"><FontAwesomeIcon icon={faDownload}
                                                                                          className="me-1"/> Download</Button>
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
                            <h1 className="fw-bolder text-secondary">Volt React Dashboard</h1>
                            <p className="text-muted fw-light mb-5 h5">Open source powered by React.js and Bootstrap
                                5</p>
                            <div className="d-flex align-items-center justify-content-center">
                                <Button variant="secondary" className="text-dark me-3">
                                    Explore dashboard <FontAwesomeIcon icon={faExternalLinkAlt}
                                                                       className="d-none d-sm-inline ms-1"/>
                                </Button>
                                <GitHubButton className="mt-lg-2"
                                              href="https://github.com/themesberg/volt-react-dashboard"
                                              data-size="large"
                                              data-show-count="true"
                                              aria-label="Star themesberg/volt-react-dashboard on GitHub">Star</GitHubButton>
                            </div>
                        </Col>
                    </Row>
                    <figure className="position-absolute bottom-0 left-0 w-100 d-none d-md-block mb-n2">
                        <svg className="fill-soft" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 3000 185.4">
                            <path d="M3000,0v185.4H0V0c496.4,115.6,996.4,173.4,1500,173.4S2503.6,115.6,3000,0z"/>
                        </svg>
                    </figure>
                </Container>
            </section>
            <div className="section pt-0">
                <Container className="mt-n10 mt-lg-n12 z-2">
                    <Row className="justify-content-center">
                        <Col xs={12}>
                            <Image src={MockupPresentation} alt="Mockup presentation"/>
                        </Col>
                    </Row>
                    <Row className="justify-content-center mt-5 mt-lg-6">
                        <Col xs={6} md={3} className="text-center mb-4">
                            <div
                                className="icon icon-shape icon-lg bg-white shadow-lg border-light rounded-circle mb-4">
                                <FontAwesomeIcon icon={faPager} className="text-secondary"/>
                            </div>
                            <h3 className="fw-bolder">10</h3>
                            <p className="text-gray">Example Pages</p>
                        </Col>
                        <Col xs={6} md={3} className="text-center mb-4">
                            <div
                                className="icon icon-shape icon-lg bg-white shadow-lg border-light rounded-circle mb-4">
                                <FontAwesomeIcon icon={faReact} className="text-secondary"/>
                            </div>
                            <h3 className="fw-bolder">100+</h3>
                            <p className="text-gray">React Components</p>
                        </Col>
                        <Col xs={6} md={3} className="text-center">
                            <div
                                className="icon icon-shape icon-lg bg-white shadow-lg border-light rounded-circle mb-4">
                                <FontAwesomeIcon icon={faSass} className="text-secondary"/>
                            </div>
                            <h3 className="fw-bolder">Workflow</h3>
                            <p className="text-gray">Sass & react-app</p>
                        </Col>
                        <Col xs={6} md={3} className="text-center">
                            <div
                                className="icon icon-shape icon-lg bg-white shadow-lg border-light rounded-circle mb-4">
                                <FontAwesomeIcon color="secondary" icon={faBootstrap} className="text-secondary"/>
                            </div>
                            <h3 className="fw-bolder">Bootstrap 5</h3>
                            <p className="text-gray">CSS Framework</p>
                        </Col>
                    </Row>
                </Container>
            </div>
            <Footer/>
        </>
    );
};

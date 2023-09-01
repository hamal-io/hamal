import React from "react";
import moment from "moment-timezone";
import {Row, Col, Card, OverlayTrigger, Tooltip, Image, Button, Container, Navbar} from '@themesberg/react-bootstrap';
import ReactHero from "../../../assets/img/technologies/react-hero-logo.svg";
import ThemesbergLogo from "../../../assets/img/themesberg-logo.svg";

interface FooterProps {
}

export default (props: FooterProps) => {
    const currentYear = moment().get("year");

    return (
        <footer className="footer py-6 bg-dark text-white">
            <Container>
                <Row>
                    <Col md={4}>
                        <Navbar.Brand className="me-lg-3 mb-3 d-flex align-items-center">
                            <Image src={ReactHero}/>
                            <span className="ms-2 brand-text">Volt React</span>
                        </Navbar.Brand>
                        <p>Volt React is a free and open source admin dashboard template powered by React.js and
                            Bootstrap 5.</p>
                    </Col>
                    <Col xs={6} md={2} className="mb-5 mb-lg-0">
                        <span className="h5">Themesberg</span>
                        <ul className="links-vertical mt-2">
                            <li><Card.Link target="_blank" href="https://themesberg.com/blog">Blog</Card.Link></li>
                            <li><Card.Link target="_blank"
                                           href="https://themesberg.com/products">Products</Card.Link></li>
                            <li><Card.Link target="_blank" href="https://themesberg.com/about">About Us</Card.Link>
                            </li>
                            <li><Card.Link target="_blank" href="https://themesberg.com/contact">Contact
                                Us</Card.Link></li>
                        </ul>
                    </Col>
                    <Col xs={6} md={2} className="mb-5 mb-lg-0">
                        <span className="h5">Other</span>
                        <ul className="links-vertical mt-2">
                            <li>
                                <Card.Link target="_blank">Getting started</Card.Link>
                            </li>
                            <li><Card.Link target="_blank">Changelog</Card.Link></li>
                            <li><Card.Link target="_blank"
                                           href="https://themesberg.com/licensing">License</Card.Link></li>
                        </ul>
                    </Col>
                    <Col xs={12} md={4} className="mb-5 mb-lg-0">
                        <span className="h5 mb-3 d-block">Subscribe</span>
                        <form action="#">
                            <div className="form-row mb-2">
                                <div className="col-12">
                                    <input type="email" className="form-control mb-2"
                                           placeholder="example@company.com" name="email"
                                           aria-label="Subscribe form" required/>
                                </div>
                                <div className="col-12">
                                    <button type="submit"
                                            className="btn btn-secondary text-dark shadow-soft btn-block"
                                            data-loading-text="Sending">
                                        <span>Subscribe</span>
                                    </button>
                                </div>
                            </div>
                        </form>
                        <p className="text-muted font-small m-0">We’ll never share your details. See our <Card.Link
                            className="text-white" href="#">Privacy Policy</Card.Link></p>
                    </Col>
                </Row>
                <hr className="bg-gray my-5"/>
                <Row>
                    <Col className="mb-md-2">
                        <Card.Link href="https://themesberg.com" target="_blank"
                                   className="d-flex justify-content-center">
                            <Image src={ThemesbergLogo} height={35} className="d-block mx-auto mb-3"
                                   alt="Themesberg Logo"/>
                        </Card.Link>
                        <div className="d-flex text-center justify-content-center align-items-center"
                             role="contentinfo">
                            <p className="font-weight-normal font-small mb-0">Copyright © Themesberg 2019-<span
                                className="current-year">2021</span>. All rights reserved.</p>
                        </div>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

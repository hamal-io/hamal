import React, {useState} from "react";
import SimpleBar from 'simplebar-react';
import {Link, useLocation} from "react-router-dom";
import {CSSTransition} from 'react-transition-group';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChartPie, faHandHoldingUsd} from "@fortawesome/free-solid-svg-icons";
import {Accordion, Badge, Button, Dropdown, Image, Nav, Navbar} from '@themesberg/react-bootstrap';

import {Routes} from "../routes";
import ReactHero from "../assets/img/technologies/react-hero-logo.svg";

export default (props = {}) => {
    const location = useLocation();
    const {pathname} = location;
    const [show, setShow] = useState(false);
    const showClass = show ? "show" : "";

    const onCollapse = () => setShow(!show);

    const CollapsableNavItem = (props) => {
        const {eventKey, title, icon, children = null} = props;
        const defaultKey = pathname.indexOf(eventKey) !== -1 ? eventKey : "";

        return (
            <Accordion as={Nav.Item} defaultActiveKey={defaultKey}>
                <Accordion.Item eventKey={eventKey}>
                    <Accordion.Button as={Nav.Link} className="d-flex justify-content-between align-items-center">
            <span>
              <span className="sidebar-icon"><FontAwesomeIcon icon={icon}/> </span>
              <span className="sidebar-text">{title}</span>
            </span>
                    </Accordion.Button>
                    <Accordion.Body className="multi-level">
                        <Nav className="flex-column">
                            {children}
                        </Nav>
                    </Accordion.Body>
                </Accordion.Item>
            </Accordion>
        );
    };

    const NavItem = (props) => {
        const {
            title,
            link,
            external,
            target,
            icon,
            image,
            badgeText,
            badgeBg = "secondary",
            badgeColor = "primary"
        } = props;
        const classNames = badgeText ? "d-flex justify-content-start align-items-center justify-content-between" : "";
        const navItemClassName = link === pathname ? "active" : "";
        const linkProps = external ? {href: link} : {as: Link, to: link};

        return (
            <Nav.Item className={navItemClassName} onClick={() => setShow(false)}>
                <Nav.Link {...linkProps} target={target} className={classNames}>
          <span>
            {icon ? <span className="sidebar-icon"><FontAwesomeIcon icon={icon}/> </span> : null}
              {image ? <Image src={image} width={20} height={20} className="sidebar-icon svg-icon"/> : null}

              <span className="sidebar-text">{title}</span>
          </span>
                    {badgeText ? (
                        <Badge pill bg={badgeBg} text={badgeColor}
                               className="badge-md notification-count ms-2">{badgeText}</Badge>
                    ) : null}
                </Nav.Link>
            </Nav.Item>
        );
    };

    return (
        <>
            <Navbar expand={false} collapseOnSelect variant="dark" className="navbar-theme-primary px-4 d-md-none">
                <Navbar.Brand className="me-lg-5" as={Link} to={Routes.Dashboard.path}>
                    <Image src={ReactHero} className="navbar-brand-light"/>
                </Navbar.Brand>
                <Navbar.Toggle as={Button} aria-controls="main-navbar" onClick={onCollapse}>
                    <span className="navbar-toggler-icon"/>
                </Navbar.Toggle>
            </Navbar>
            <CSSTransition timeout={300} in={show} classNames="sidebar-transition">
                <SimpleBar className={`collapse ${showClass} sidebar d-md-block bg-primary text-white`}>
                    <div className="sidebar-inner px-4 pt-3">
                        <Nav className="flex-column pt-3 pt-md-0">

                            <NavItem title="Overview" link={Routes.Dashboard.path} icon={faChartPie}/>
                            <NavItem title="Adhoc" icon={faHandHoldingUsd} link={Routes.Executions.path}/>
                            <NavItem title="Execution" icon={faHandHoldingUsd} link={Routes.Executions.path}/>
                            <NavItem title="Log" icon={faHandHoldingUsd} link={Routes.Executions.path}/>

                            <Dropdown.Divider className="my-3 border-indigo"/>

                            <NavItem external title="Extension"
                                     link="https://demo.themesberg.com/volt-pro-react/#/plugins/datatable"
                                     target="_blank"/>


                        </Nav>
                    </div>
                </SimpleBar>
            </CSSTransition>
        </>
    );
};

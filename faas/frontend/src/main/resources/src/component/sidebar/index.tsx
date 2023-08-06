import React, {useState} from "react";
import SimpleBar from 'simplebar-react';
import {useLocation} from "react-router-dom";
import {CSSTransition} from 'react-transition-group';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {
    faBook,
    faBoxOpen,
    faChartPie,
    faCog,
    faFileAlt,
    faHandHoldingUsd,
    faSignOutAlt,
    faTable,
    faTimes,
    faCalendarAlt,
    faMapPin,
    faInbox,
    faRocket, faBinoculars, faCode
} from "@fortawesome/free-solid-svg-icons";
import {Nav, Badge, Image, Button, Dropdown, Accordion, Navbar} from '@themesberg/react-bootstrap';
import {Link} from 'react-router-dom';

import {Routes} from "../../routes";

export default (props = {}) => {
    const location = useLocation();
    const {pathname} = location;
    const [show, setShow] = useState(false);
    const showClass = show ? "show" : "";

    const onCollapse = () => setShow(!show);

    const CollapsableNavItem = (props: any) => {
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

    const NavItem = (props: any) => {
        const {
            title,
            link,
            target,
            icon,
            image,
            badgeText,
            badgeBg = "secondary",
            badgeColor = "primary"
        } = props;
        const classNames = badgeText ? "d-flex justify-content-start align-items-center justify-content-between" : "";
        const navItemClassName = link === pathname ? "active" : "";
        return (
            <Nav.Item className={navItemClassName} onClick={() => setShow(false)}>
                <Nav.Link as={Link} to={link} target={target} className={classNames}>
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
                </Navbar.Brand>
                <Navbar.Toggle as={Button} aria-controls="main-navbar" onClick={onCollapse}>
                    <span className="navbar-toggler-icon"/>
                </Navbar.Toggle>
            </Navbar>
            <CSSTransition timeout={300} in={show} classNames="sidebar-transition">
                <SimpleBar className={`collapse ${showClass} sidebar d-md-block bg-primary text-white`}>
                    <div className="sidebar-inner px-4 pt-3">
                        <div
                            className="user-card d-flex d-md-none align-items-center justify-content-between justify-content-md-center pb-4">
                            <div className="d-flex align-items-center">
                                <div className="user-avatar lg-avatar me-4">
                                </div>
                                <div className="d-block">
                                </div>
                            </div>
                            <Nav.Link className="collapse-close d-md-none" onClick={onCollapse}>
                                <FontAwesomeIcon icon={faTimes}/>
                            </Nav.Link>
                        </div>
                        <Nav className="flex-column pt-3 pt-md-0">
                            <NavItem title="Overview" link={Routes.Dashboard.path} icon={faChartPie}/>
                            <NavItem title="Adhoc" link={Routes.Adhoc.path} icon={faRocket}/>
                            <NavItem title="Executions" link={Routes.Executions.path} icon={faBinoculars}/>
                            <NavItem title="Functions" link={Routes.Functions.path} icon={faCode}/>
                        </Nav>
                    </div>
                </SimpleBar>
            </CSSTransition>
        </>
    );
};

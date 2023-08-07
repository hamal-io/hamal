import React, {useEffect, useState} from "react";
import Preloader from "../preloader";
import Sidebar from "../sidebar";
import Footer from "../footer";
import {Badge, Container, Image, Nav,  Tab, TabPane} from "@themesberg/react-bootstrap";
import {CSSTransition} from "react-transition-group";
import SimpleBar from "simplebar-react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import {Routes} from "../../routes";
import {Link} from "react-router-dom";
import Navbar from "../navbar";

export const RouteWithSidebar = (props: any) => {
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => setLoaded(true), 1000);
        return () => clearTimeout(timer);
    }, []);

    const localStorageIsSettingsVisible = () => {
        return localStorage.getItem('settingsVisible') !== 'false'
    }

    const [showSettings, setShowSettings] = useState(localStorageIsSettingsVisible);

    const toggleSettings = () => {
        setShowSettings(!showSettings);
        localStorage.setItem('settingsVisible', String(!showSettings));
    }

    const {pathname} = location;
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
            <Nav.Item className={navItemClassName}>
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
            <Preloader show={!loaded}/>

            <Navbar/>


            {/*<Navbar expand="lg" bg="dark" className="navbar-transparent navbar-theme-light my-2">*/}
            {/*    <Container className="position-relative">*/}
            {/*        <Navbar.Collapse id="navbar-default-secondary" className="w-10">*/}
            {/*            <Nav className="navbar-nav-hover align-items-sm-center">*/}
            {/*                <Nav.Link href="#home">Home</Nav.Link>*/}
            {/*                <Nav.Link href="#about">About</Nav.Link>*/}
            {/*                <Nav.Link href="#contact">Contact</Nav.Link>*/}
            {/*            </Nav>*/}
            {/*        </Navbar.Collapse>*/}
            {/*        <Navbar.Toggle aria-controls="navbar-default-primary"/>*/}
            {/*    </Container>*/}
            {/*</Navbar>*/}

            {/*<Sidebar/>*/}
            <main className="content">



                {props.component}
                <Footer toggleSettings={toggleSettings} showSettings={showSettings}/>
            </main>
        </>
    )

}


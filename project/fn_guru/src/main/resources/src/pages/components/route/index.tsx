import React, {useEffect, useState} from "react";
import Preloader from "../../../components/preloader";
import {Badge, Image, Nav} from "@themesberg/react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Link} from "react-router-dom";
import Navbar from "../navbar";
import Sidebar from "../sidebar";

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
            {/*<Preloader show={!loaded}/>*/}
            <Sidebar/>
            <main className="content">
                <Navbar/>
                {props.component}
            </main>
        </>
    )
}


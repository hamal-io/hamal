import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {Container, Form, InputGroup, Nav, Navbar} from '@themesberg/react-bootstrap';


export default (props) => {
    const [notifications, setNotifications] = useState([]);
    const areNotificationsRead = notifications.reduce((acc, notif) => acc && notif.read, true);

    const markNotificationsAsRead = () => {
        setTimeout(() => {
            setNotifications(notifications.map(n => ({...n, read: true})));
        }, 300);
    };


    return (
        <Navbar variant="dark" expanded className="ps-0 pe-2 pb-0">
            <Container fluid className="px-0">
                <div className="d-flex justify-content-between w-100">
                    <div className="d-flex align-items-center">
                    </div>
                    <Nav className="align-items-center">
                    </Nav>
                </div>
            </Container>
        </Navbar>
    );
};

import React, {useEffect, useState} from "react";
import {useNavigate} from 'react-router-dom';
import {
    Breadcrumb,
    Button,
    ButtonGroup,
    Col,
    Dropdown,
    DropdownButton,
    Form,
    InputGroup,
    Row
} from '@themesberg/react-bootstrap';
import {Modal} from '@themesberg/react-bootstrap';
import {ExecutionTable} from "./table";
import {faHome, faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {createTrigger, listTriggers} from "../../api";

import {ApiSimpleTrigger} from "../../api/types";
import {State} from "./state";


export default () => {
    const [triggers, setTriggers] = useState([] as Array<ApiSimpleTrigger>)
    useEffect(() => {
        listTriggers({limit: 1000}).then(response => {
            setTriggers(response.triggers)
        })
    }, []);

    const [showFixedRateModal, setShowFixedRateModal] = useState(false);
    const handleFixedRateClose = () => setShowFixedRateModal(false);


    const [showEventModal, setShowEventModal] = useState(false);
    const handleEventClose = () => setShowEventModal(false);

    return (
        <State.Provider value={triggers}>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center py-2">
                <div className="d-block mb-4 mb-md-0">
                    <Breadcrumb className="d-none d-md-inline-block"
                                listProps={{className: "breadcrumb-dark breadcrumb-transparent"}}>
                        <Breadcrumb.Item><FontAwesomeIcon icon={faHome}/></Breadcrumb.Item>
                        <Breadcrumb.Item>Triggers</Breadcrumb.Item></Breadcrumb>
                </div>
            </div>

            <div className="table-settings mb-4">
                <Row className="justify-content-between align-items-center">
                    <Col xs={8} md={6} lg={3} xl={4}>
                        <InputGroup>
                            <InputGroup.Text>
                                <FontAwesomeIcon icon={faSearch}/>
                            </InputGroup.Text>
                            <Form.Control type="text" placeholder="Search"/>
                        </InputGroup>
                    </Col>
                    <Col xs={4} md={6} lg={3} xl={4}>
                        <ButtonGroup className={"float-end"}>
                            <Button onClick={() => setShowFixedRateModal(true)} variant="primary" className="m-1"> + Fixed Rate</Button>
                            <Button onClick={() => setShowEventModal(true)} variant="primary" className="m-1"> + Event</Button>
                        </ButtonGroup>
                    </Col>
                </Row>
            </div>

            <ExecutionTable/>

            {
                <CreateFixedRateTriggerModal visible={showFixedRateModal} onClose={handleFixedRateClose}/>
            }
            {
                <CreateEventTriggerModal visible={showEventModal} onClose={handleEventClose}/>
            }
        </State.Provider>
    );
};

interface CreateFixedRateTriggerModalProps {
    visible: boolean;
    onClose: () => void
}

const CreateFixedRateTriggerModal = (props: CreateFixedRateTriggerModalProps) => {
    const [name, setName] = useState('')
    const [funcId, setFuncId] = useState('')
    const [duration, setDuration] = useState('PT5S')

    const navigate = useNavigate()

    const submit = () => {
        createTrigger({name, id: funcId, duration, type: "FixedRate"}).then(response => {
            console.log(response)
            navigate(`/triggers/${response.id}`)
        })
    }

    return (
        <React.Fragment>
            <Modal as={Modal.Dialog} centered show={props.visible} onHide={props.onClose}>
                <Modal.Header>
                    <Modal.Title className="h6">New fixed rate trigger</Modal.Title>
                    <Button variant="close" aria-label="Close" onClick={props.onClose}/>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                type="text"
                                value={name}
                                onChange={evt => setName(evt.target.value)}
                                placeholder="Trigger name..."
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>FuncId</Form.Label>
                            <Form.Control
                                type="text"
                                value={funcId}
                                onChange={evt => setFuncId(evt.target.value)}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Duration</Form.Label>
                            <Form.Control
                                type="text"
                                value={duration}
                                onChange={evt => setDuration(evt.target.value)}
                            />
                        </Form.Group>

                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={submit}>
                        Create
                    </Button>
                </Modal.Footer>
            </Modal>
        </React.Fragment>
    )
}


interface CreateEventTriggerModalProps {
    visible: boolean;
    onClose: () => void
}

const CreateEventTriggerModal = (props: CreateEventTriggerModalProps) => {
    const [name, setName] = useState('')
    const [funcId, setFuncId] = useState('')
    const [topicId, setTopicId] = useState('')

    const navigate = useNavigate()

    const submit = () => {
        createTrigger({name, id: funcId, topicId, type: "Event"}).then(response => {
            console.log(response)
            navigate(`/triggers/${response.id}`)
        })
    }

    return (
        <React.Fragment>
            <Modal as={Modal.Dialog} centered show={props.visible} onHide={props.onClose}>
                <Modal.Header>
                    <Modal.Title className="h6">New event trigger</Modal.Title>
                    <Button variant="close" aria-label="Close" onClick={props.onClose}/>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                type="text"
                                value={name}
                                onChange={evt => setName(evt.target.value)}
                                placeholder="Trigger name..."
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>FuncId</Form.Label>
                            <Form.Control
                                type="text"
                                value={funcId}
                                onChange={evt => setFuncId(evt.target.value)}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Topic Id</Form.Label>
                            <Form.Control
                                type="text"
                                value={topicId}
                                onChange={evt => setTopicId(evt.target.value)}
                            />
                        </Form.Group>

                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={submit}>
                        Create
                    </Button>
                </Modal.Footer>
            </Modal>
        </React.Fragment>
    )
}

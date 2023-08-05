import React from "react";

interface EditorProps {
    code: string;
}

interface EditorState {
    code: string;
}

export default class Editor extends React.Component<EditorProps, EditorState> {
    constructor(props: EditorProps) {
        super(props);
        this.state = {
            code: props.code,
        }
    }

    render() {
        return (
            <h1> EDITOR</h1>
        );
    }
}

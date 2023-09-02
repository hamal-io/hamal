import React, {useState} from "react";
import {ApiFunction} from "../../../../api/types";
import Editor from "../../../../components/editor";

export interface CodeTabProps {
    func: ApiFunction
}

export default (props: CodeTabProps) => {
    const [code, setCode] = useState(props.func.code.value || '')
    return (
        <Editor
            code={code}
            onChange={code => setCode(code || '')}
        />
    )
}
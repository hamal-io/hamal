import {Button} from "@/components/ui/button"
import {useApiPatch} from "@/hook";
import {FC} from "react";

interface ApiFuncUpdateSubmitted {
    id: string;
    status: string;
    funcId: string;
}

type Props = {
    funcId: string;
    code: string;
    name: string;
}

const Save: FC<Props> = ({funcId, code, name}) => {
    const [updateFunc] = useApiPatch<ApiFuncUpdateSubmitted>()
    return (
        <Button onClick={() => {
            updateFunc(`v1/funcs/${funcId}`, {
                name,
                code
            })
        }}> Save </Button>
    )
}

export default Save;
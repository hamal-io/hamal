import {Button} from "@/components/ui/button"
import {useFuncUpdate} from "@/hook";
import {FC} from "react";

type Props = {
    funcId: string;
    code: string;
    name: string;
}

const Save: FC<Props> = ({funcId, code, name}) => {
    const [updateFunc] = useFuncUpdate()
    return (
        <Button
            variant="secondary"
            onClick={() => {
            updateFunc(funcId, name, code)
        }}> Save </Button>
    )
}

export default Save;
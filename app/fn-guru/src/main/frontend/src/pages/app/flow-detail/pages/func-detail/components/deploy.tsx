import {Button} from "@/components/ui/button"
import {useApiDeployLatestCode} from "@/hook";
import {FC} from "react";

type Props = {
    funcId: string;
}

const Deploy: FC<Props> = ({funcId}) => {
    const [deployFunc] = useApiDeployLatestCode()
    return (
        <Button
            variant="secondary"
            onClick={() => deployFunc(funcId)}
        >Deploy</Button>
    )
}

export default Deploy;
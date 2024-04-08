import React, {FC} from "react";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";

type Props = {
    name: string;
    description: string;
}
export const FlowCard: FC<Props> = ({name, description}) => {
    const navigate = useNavigate()

    function handleClick() {
        //navigate("/")
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>{name}</CardTitle>
            </CardHeader>
            <CardContent>
                <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                    <div className="flex justify-between py-3 gap-x-4">
                        {description}
                    </div>
                </dl>
            </CardContent>
        </Card>
    )
}
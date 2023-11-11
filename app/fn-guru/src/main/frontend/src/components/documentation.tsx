ximport
{
    Button
}
from
"@/components/ui/button.tsx";
import {BookOpen} from "lucide-react";
import React, {FC} from "react";

type Props = {
    link: string;
}


export const GoToDocumentation: FC<Props> = ({link}) => (
    <Button variant="secondary" className="items-center gap-2 " onClick={() => {
        const url = `https://doc.fn.guru/${link}`
        window.open(url, "_blank") || window.location.replace(url);
    }}>
        <BookOpen className="w-4 h-4 md:w-5 md:h-5"/>
        Documentation
    </Button>
)
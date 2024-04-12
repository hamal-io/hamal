import {tags} from "@/pages/app/recipe-list/components/tags.ts";
import {FC, ReactSVGElement, useEffect, useRef, useState} from "react";
import {Card} from "@/components/ui/card.tsx";

const TagFilter = () => {
    const [selected, setSelected] = useState([])

    function handleSelect(id: number) {
        setSelected(prevState => [...prevState, id])
    }

    return (
        <div className={"tagBox"}>
            {
                tags.map(tag => (
                    <TagCard key={tag.id} id={tag.id} name={tag.name} icon={tag.icon} onSelect={handleSelect}></TagCard>
                ))
            }
        </div>
    )
}

export default TagFilter

type TagCardProps = {
    id: number,
    name: string,
    icon: ReactSVGElement,
    onSelect: (id: number) => void
}
const TagCard: FC<TagCardProps> = ({id, name, icon, onSelect}) => {
    const [selected, setSelected] = useState(false)
    const cardRef = useRef(null)

    useEffect(() => {
        if (selected === true) {
            cardRef.current.style.background = "blue"
            cardRef.current.style.color = "white"
        }
    }, [selected]);


    function handleClick() {
        setSelected(!selected)
        onSelect(id)
    }

    return (
        <Card onClick={handleClick}>
            <div ref={cardRef} className={"flex-row gap-4"}>
                <div>
                    {icon}
                </div>
                <div>{name}</div>
            </div>
        </Card>
    )
}
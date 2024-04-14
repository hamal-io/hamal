import {FC, useEffect, useRef, useState} from "react";
import {Card} from "@/components/ui/card.tsx";
import styles from "./tag.module.css"
import {tags} from "@/pages/app/recipe-list/components/tags.tsx";
import {Tag} from "@/types/recipe.ts";

const TagFilter = () => {
    const [selected, setSelected] = useState([])

    function handleSelect(id: number) {
        setSelected(prevState => [...prevState, id])
    }

    return (
        <div className={styles.box}>
            {
                Object.entries(tags).map(([, tag]) => (
                    <TagCard key={tag.id} tag={tag} onSelect={handleSelect}></TagCard>
                ))
            }
        </div>
    )
}

export default TagFilter

type TagCardProps = {
    tag: Tag
    onSelect: (id: number) => void
}
const TagCard: FC<TagCardProps> = ({tag, onSelect}) => {
    const [selected, setSelected] = useState(false)
    const cardRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (selected === true) {
            cardRef.current.classList.add(styles.selected)
        } else {
            cardRef.current.classList.remove(styles.selected)
        }
    }, [selected]);


    function handleClick() {
        setSelected(!selected)
        onSelect(tag.id)
    }

    return (
        <Card ref={cardRef} className={styles.tag} onClick={handleClick}>
            <div>
                {tag.icon}
            </div>
            <div>{tag.name}</div>
        </Card>
    )
}
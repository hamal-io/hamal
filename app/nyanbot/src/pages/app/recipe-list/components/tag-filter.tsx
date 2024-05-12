import {FC, useEffect, useRef, useState} from "react";
import {Card} from "@/components/ui/card.tsx";
import styles from "./tag.module.css"
import {Tag} from "@/types/recipe.ts";
import {tags} from "@/pages/app/recipe-list/components/tags.tsx";

type Props = {
    onChange: (tags: Set<string>) => void
}
const TagFilter: FC<Props> = ({onChange}) => {
    const [selectedTags, setSelectedTags] = useState<Set<string>>(new Set())

    function handleSelect(name: string) {
        const newSet = new Set(selectedTags)
        if (selectedTags.has(name)) {
            newSet.delete(name)
        } else {
            newSet.add(name)
        }
        setSelectedTags(newSet)
        onChange(newSet)
    }

    return (
        <div>
            <ol className={styles.box}>
                {
                    Object.entries(tags).map(([, tag]) => (
                        <TagCard key={tag.id} tag={tag} onSelect={handleSelect}></TagCard>
                    ))
                }
            </ol>
        </div>
    )
}

export default TagFilter

type TagCardProps = {
    tag: Tag
    onSelect: (id: string) => void
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
        onSelect(tag.name)
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
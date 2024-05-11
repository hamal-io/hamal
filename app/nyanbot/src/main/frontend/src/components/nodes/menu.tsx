import React, {FC} from "react";
import styles from "./menu.module.css";
import {Button} from "@/components/ui/button.tsx";

type MenuProps = {}

export const Menu: FC<MenuProps> = ({}) => {
    return (
        <div className={styles.menu}>
            <Button> Add Node </Button>
            <Button> Add Node </Button>
            <Button> Add Node </Button>
            {/*<NodeLibrary/>*/}
        </div>
    )
}

type NodeLibraryProps = {}

const NodeLibrary: FC<NodeLibraryProps> = ({}) => {
    return (
        <div className={styles.parent}>
            <img src="https://i.picsum.photos/id/995/536/354.jpg?hmac=kARkIcQD-5FYzmRwd89uPn6yxoJvaCg43bkO-kABGGE" alt="" className={styles.child}/>
            <span className={styles.text}>Lorem text</span>
        </div>
    );
}
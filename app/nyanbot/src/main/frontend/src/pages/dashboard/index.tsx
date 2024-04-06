import React from "react";
import {Node} from '@/components/nodes/node.tsx'
import {Comment} from '@/components/nodes/comment.tsx'
import {Canvas} from "@/components/nodes/canvas.tsx";

import styles from './styles.module.css'

const DashboardSite = () => {
    return (
        <div className="w-screen h-screen">

            <Canvas
                // position={{x: 0, y: 80}} // offsets menu navbar
            >

                {/*<Comment*/}
                {/*    id='3'*/}
                {/*    text="Hello, I am your favourite commentt"*/}
                {/*    position={{x: 100, y: 100}}*/}
                {/*    size={{width: 250, height: 100}}*/}
                {/*    onDragStart={() => console.log("start dragging")}*/}
                {/*/>*/}

                <Node
                    id='1'
                    type="test"
                    position={{x: 0, y: 0}}
                    size={{width: 100, height: 100}}
                    onDragStart={() => console.log("start dragging")}
                />

                {/*<Node*/}
                {/*    id='2'*/}
                {/*    type="test"*/}
                {/*    position={{x: -400, y: -500}}*/}
                {/*    size={{width: 250, height: 100}}*/}
                {/*    onDragStart={() => console.log("start dragging")}*/}
                {/*/>*/}

            </Canvas>

        </div>
    )
}

export default DashboardSite
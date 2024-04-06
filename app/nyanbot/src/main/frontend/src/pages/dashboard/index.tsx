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

                <Node
                    id='1'
                    type="test"
                    position={{x: -400, y: 0}}
                    size={{width: 200, height: 100}}
                    onDragStart={() => console.log("start dragging")}
                />

                <Node
                    id='2'
                    type="test"
                    position={{x: -100, y: 0}}
                    size={{width: 250, height: 100}}
                    onDragStart={() => console.log("start dragging")}
                />

                <Node
                    id='3'
                    type="TELEGRAM_SEND_MESSAGE"
                    position={{x: 250, y: 0}}
                    size={{width: 150, height: 100}}
                    onDragStart={() => console.log("start dragging")}
                />

            </Canvas>

        </div>
    )
}

export default DashboardSite
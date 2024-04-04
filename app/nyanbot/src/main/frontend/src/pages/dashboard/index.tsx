import React from "react";
import {Node} from '@/components/nodes/node.tsx'
import {Canvas} from "@/components/nodes/canvas.tsx";

const DashboardSite = () => {
    return (
        <div className="w-screen h-screen">

            {/*<Comment*/}
            {/*    text="Hello nyanbot"*/}
            {/*    position={{x: 100, y: 100}}*/}
            {/*    size={{width: 250, height: 100}}*/}
            {/*/>*/}

            <Canvas
                position={{x: 0, y: 0}}
            >

                <Node
                    id='1'
                    type="test"
                    position={{x: -400, y: 500}}
                    size={{width: 250, height: 100}}
                />

                <Node
                    id='2'
                    type="test"
                    position={{x: -400, y: -500}}
                    size={{width: 250, height: 100}}
                />

            </Canvas>

        </div>
    )
}

export default DashboardSite
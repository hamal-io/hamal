import React from "react";
import {Comment} from '@/components/nodes/comment.tsx'
import {Node} from '@/components/nodes/node.tsx'

const DashboardSite = () => {
    return (
        <div className="h-screen" style={{background: "yellow"}}>
            Hallo Dashboard

            <Comment
                text="Hello nyanbot"
                position={{x: 100, y: 100}}
                size={{width: 250, height: 100}}
            />

            <Node
                id='1'
                type="test"
                position={{x: 400, y: 400}}
                size={{width: 250, height: 100}}
            />

            <Node
                id='2'
                type="test"
                position={{x: 900, y: 400}}
                size={{width: 250, height: 100}}
            />

        </div>
    )
}

export default DashboardSite
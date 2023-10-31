import React from 'react'
import {useParams} from "react-router-dom";

const NmaespaceFuncDetailPage: React.FC = () => {
    const {funcId} = useParams()


    return (
        <div className="flex-1  w-full mx-auto h-full bg-gray-100">

            <div className="flex flex-col items-center justify-center">
                <div className="w-full">
                    {funcId}
                </div>

            </div>
        </div>

    );
}


export default NmaespaceFuncDetailPage


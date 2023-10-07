import React from 'react'
import {useParams} from "react-router-dom";

const FuncDetailPage: React.FC = () => {
    const {funcId} = useParams()
    return (
        <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
            <div className="flex flex-col items-center justify-center">
                <p> Func Detail Page {funcId} </p>
            </div>
        </main>
    );
}

export default FuncDetailPage


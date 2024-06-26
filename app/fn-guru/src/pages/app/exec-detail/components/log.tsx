import {FC, useEffect} from "react";
import {useExecLogList} from "@/hook";

type Props = {
    execId: string;
}

const Log: FC<Props> = ({execId}) => {
    const [listExecLogs, execLogsList, loading] = useExecLogList()

    useEffect(() => {
        listExecLogs(execId)
    }, [execId]);

    if (execLogsList == null || loading) return "Loading..."
    return (
        <div className="w-full">
            <div
                className="coding inverse-toggle px-5 pt-4 text-gray-100 text-sm font-mono subpixel-antialiased bg-gray-50  pb-6  rounded-lg leading-normal overflow-hidden">
                {execLogsList.logs.map(log => {
                    return (
                        <div key={log.id} className="flex">
                            <span className="text-gray-500 mr-2">{new Date(log.timestamp).toISOString()}</span>
                            <LogLevel level={log.level}/>
                            <span className="text-gray-500 ml-2">{log.message}</span>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

const LogLevel = ({level}: { level: string }) => {
    switch (level) {
        case 'Trace':
            return <span className="text-gray-500">Trace</span>
        case 'Debug':
            return <span className="text-green-400">Debug</span>
        case 'Info':
            return <span className="text-blue-400">Info</span>
        case 'Warn':
            return <span className="text-yellow-400">Warn</span>
        case 'Error':
            return <span className="text-red-400">Error</span>
        case 'Fatal':
            return <span className="text-red-900">Fatal</span>
    }
}


export default Log;
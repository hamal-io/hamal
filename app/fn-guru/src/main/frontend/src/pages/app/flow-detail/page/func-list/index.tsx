// import React, {useEffect, useState} from 'react'
// import {ApiFuncList} from "../../../../../api/types";
// import {useNavigate, useParams} from "react-router-dom";
// import {useApiGet, useApiPost} from "../../../../../hook";
//
// export interface ApiFuncCreateSubmitted {
//     id: string;
//     name: string;
//     funcId: string
// }
//
//
// const FlowFuncListPage: React.FC = () => {
//     const {flowId} = useParams()
//     const navigate = useNavigate()
//
//     const [funcs, isLoading, error] = useApiGet<ApiFuncList>(`v1/flows/${flowId}/funcs`)
//     if (isLoading) return "Loading..."
//     const list = funcs.funcs.map(func => (
//         <Card
//             key={func.id}
//             className="w-2/3 my-1 text-gray-900 hover:bg-gray-900  hover:text-gray-50 shadow-gray-200"
//             onClick={() => navigate(`/flows/${flowId}/functions/${func.id}`)}
//         >
//             <h5 className="text-2xl font-bold tracking-tight">
//                 <p>{func.name}</p>
//             </h5>
//         </Card>
//     ))
//
//     return (
//         <main className="flex-1 w-full mx-auto text-lg h-full bg-gray-200">
//             <section className="flex p-3 items-center justify-between">
//                 <div className="sm:flex sm:items-center sm:justify-between ">
//                     <div>
//                         <div className="flex item-sta gap-x-3">
//                             <h2 className="text-lg font-medium text-gray-800 dark:text-white">Functions</h2>
//                         </div>
//                         <p className="mt-1 text-sm text-gray-500 dark:text-gray-300">Contains workflow logic</p>
//                     </div>
//
//                 </div>
//                 <div className="flex items-center justify-end mt-4 gap-x-3">
//                     <CreateFunctionModalButton flowId={flowId}/>
//                 </div>
//             </section>
//
//             <div className="flex flex-col items-start ">
//                 {list}
//             </div>
//         </main>
//     );
// }
//
//
// const CreateFunctionModalButton = ({flowId}: { flowId: string }) => {
//     const navigate = useNavigate()
//     const [name, setName] = useState<string | undefined>()
//     const [openModal, setOpenModal] = useState<string | undefined>();
//     const props = {openModal, setOpenModal};
//
//     useEffect(() => {
//         const close = (e) => {
//             if (e.keyCode === 27) {
//                 props.setOpenModal(undefined)
//             }
//         }
//         window.addEventListener('keydown', close)
//         return () => window.removeEventListener('keydown', close)
//     }, [])
//
//     const [post, data, isLoading, error] = useApiPost<ApiFuncCreateSubmitted>()
//     useEffect(() => {
//         if (data != null) {
//             navigate(`/flows/${flowId}/functions/${data.funcId}`)
//             setOpenModal(undefined)
//         }
//
//         if (error != null) {
//             console.log("error", error)
//         }
//
//     }, [data, navigate, error]);
//
//
//     return (
//         <>
//             <Button
//                 className={"bg-gray-400"}
//                 color={"dark"}
//                 outline
//                 onClick={() => props.setOpenModal('default')}>
//                 <HiPlus className="mr-2 h-5 w-5"/>
//                 Add Function
//             </Button>
//
//             <Modal show={props.openModal === 'default'} onClose={() => props.setOpenModal(undefined)}>
//                 <Modal.Header>Create new function</Modal.Header>
//                 <Modal.Body>
//                     <div className="space-y-6">
//                         <div>
//                             <div className="mb-2 block">
//                                 <Label htmlFor="name" value="Function name"/>
//                             </div>
//                             <TextInput
//                                 id="name"
//                                 placeholder="Useful function name..."
//                                 required
//                                 onChange={evt => setName(evt.target.value)}
//                             />
//                         </div>
//                     </div>
//                 </Modal.Body>
//                 <Modal.Footer>
//                     <Button className={"w-full"} onClick={() => {
//                         post(`v1/flows/${flowId}/funcs`, {name, inputs: {}, code: ""})
//                     }}>Create Function</Button>
//                 </Modal.Footer>
//             </Modal>
//         </>
//     )
// }
//
//
// export default FlowFuncListPage
//

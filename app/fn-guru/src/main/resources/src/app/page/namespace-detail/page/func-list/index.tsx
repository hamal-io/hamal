import React from 'react'
import {ApiFuncList} from "../../../../../api/types";
import {Card} from "flowbite-react";
import {useNavigate, useParams} from "react-router-dom";
import {useApiGet} from "../../../../../hook";

const NamespaceFuncListPage: React.FC = () => {
    const {namespaceId} = useParams()
    const navigate = useNavigate()

    const [funcs, isLoading, error] = useApiGet<ApiFuncList>(`v1/namespaces/${namespaceId}/funcs`)


    if (isLoading) return "Loading..."


    const list = funcs.funcs.map(func => (
        <Card
            key={func.id}
            className="max-w-sm"
            onClick={() => navigate(`/namespaces/${namespaceId}/functions/${func.id}`)}
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{func.name}</p>
            </h5>
            <p className="font-normal text-gray-700 dark:text-gray-400">
                TBD: Here is some description
            </p>
        </Card>
    ))

    return (
        <div className="flex-1 w-full mx-auto text-lg h-full bg-gray-100">
            <div className="flex p-3 items-center justify-center bg-white">
                {/*<CreateFuncModalButton/>*/}
            </div>

            <div className="flex flex-col items-center justify-center">
                {list}
            </div>
        </div>
    );
}


// const CreateFuncModalButton = () => {
//     const {namespaceId} = useParams()
//
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
//     const submit = () => {
//         createFunc({name, namespaceId})
//             .then(response => {
//                 navigate(`/namespaces/${namespaceId}/functions/${response.id}`)
//                 props.setOpenModal(undefined)
//             })
//             .catch(console.error)
//     }
//
//     return (
//         <>
//             <Button onClick={() => props.setOpenModal('default')}>New Function</Button>
//             <Modal show={props.openModal === 'default'} onClose={() => props.setOpenModal(undefined)}>
//                 <Modal.Header>Create a new function</Modal.Header>
//                 <Modal.Body>
//                     <div className="space-y-6">
//                         <div>
//                             <div className="mb-2 block">
//                                 <Label htmlFor="name" value="Function name"/>
//                             </div>
//                             <TextInput id="name" placeholder="Useful function name..." required onChange={evt => setName(evt.target.value)}/>
//                         </div>
//                     </div>
//                 </Modal.Body>
//                 <Modal.Footer>
//                     <Button className={"w-full"} onClick={submit}>Create Function</Button>
//                 </Modal.Footer>
//             </Modal>
//         </>
//     )
// }

export default NamespaceFuncListPage


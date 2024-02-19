import React, {FC, useEffect, useState} from "react";
import {useBlueprintCreate, useBlueprintGet, useBlueprintList} from "@/hook/blueprint.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useNavigate} from "react-router-dom";
import {Plus} from "lucide-react";
import {useUiState} from "@/hook/ui-state.ts";
import {useAdhoc} from "@/hook";
import {BlueprintListItem} from "@/types/blueprint.ts";
import {Dialog, DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";

const BlueprintListPage = () => {
    const [listBlueprints, blueprintList, loading, error] = useBlueprintList()

    useEffect(() => {
        const abortController = new AbortController();
        listBlueprints(abortController)
        return () => {
            abortController.abort();
        }
    }, [listBlueprints]);


    if (error) return `Error`
    if (blueprintList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Blueprints"
                description={'Tryout our predefined workflows, proudly brought to you by the hamal.io team.'}
                actions={[<CreateBlueprint/>]}
            />
            {
                blueprintList.blueprints.length ?
                    (<BlueprintCards blueprints={blueprintList.blueprints}/>) : (<NoContent/>)
            }


        </div>
    );
}

type CardProps = {
    blueprints: Array<BlueprintListItem>
}
const BlueprintCards: FC<CardProps> = ({blueprints}) => {
    const [dialog, setDialog] = useState(null);
    const [id, setId] = useState('')

    const handleClick = (id: string) => {
        setId(id)
        setDialog(true)
    }

    return (
        <ul className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {blueprints.map((bp) => (
                <Card
                    key={bp.id}
                    onClick={() => handleClick(bp.id)}
                    className="relative overfunc-hidden duration-500 hover:border-primary/50 group"

                >
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle>{bp.name}</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                            <div className="flex justify-between py-3 gap-x-4">
                                {bp.description}
                            </div>
                        </dl>
                        <div className="flex flex-row justify-between items-center">
                        </div>
                    </CardContent>
                </Card>
            ))}
            {dialog && <Dialog open={dialog} onOpenChange={setDialog}>
                <DDialog id={id}></DDialog>
            </Dialog>}
        </ul>)
}

type DProps = { id: string }
const DDialog: FC<DProps> = ({id}) => {
    const [getBlueprint, blueprint, loading, error] = useBlueprintGet()
    const navigate = useNavigate()

    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(id, abortController)
        return () => {
            abortController.abort();
        }
    }, [id]);

    if (blueprint == null || loading) return "Loading..."

    return (
        <DialogContent>
            <DialogHeader>{blueprint.name}</DialogHeader>
            <p>{blueprint.description}</p>
            <Deploy blueprintId={blueprint.id}/>
            <Button size={"sm"}
                    onClick={() => {
                        navigate(`/blueprints/editor/${blueprint.id}`)
                    }} variant="secondary">
                Config
            </Button>
        </DialogContent>
    )
}


const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Blueprints found.</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>

        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <CreateBlueprint/>
        </div>
    </EmptyPlaceholder>
)

const CreateBlueprint = () => {
    const navigate = useNavigate()
    const [isLoading, setLoading] = useState(false)
    const [createBlueprint, submitted] = useBlueprintCreate()

    async function create() {
        setLoading(true)
        try {
            createBlueprint("New Blueprint", "print('hamal')", "I will print 'hamal'")
        } catch (e) {
            console.log(e)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const abortController = new AbortController();
        if (submitted !== null) {
            navigate(`/blueprints/editor/${submitted.id}`)
        }
        return () => {
            abortController.abort();
        }
    }, [submitted, navigate]);

    return (
        <Button type={"submit"} size="lg" onClick={create}>
            <Plus className="w-4 h-4 mr-1"/>
            Create Blueprint
        </Button>
    )
}

type DeployProps = { blueprintId: string }
const Deploy: FC<DeployProps> = ({blueprintId}) => {
    const [uiState] = useUiState()
    const [adhoc, data] = useAdhoc()
    const [getBlueprint, blueprint, loading, error] = useBlueprintGet()


    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(blueprintId)
        return () => {
            abortController.abort();
        };
    }, [blueprintId]);

    async function setup() {
        if (blueprint !== null) {
            adhoc(uiState.namespaceId, blueprint.value)
        }
    }

    return (
        <Button type={"submit"} size="sm" onClick={setup}>
            Deploy
        </Button>
    )
}


export default BlueprintListPage
import React, {FC} from "react";
import {useBlueprintList} from "@/hook/blueprint.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {BlueprintListItem} from "@/types/blueprint.ts";
import {PageHeader} from "@/components/page-header.tsx";

type Props = {}
const BlueprintListPage: FC<Props> = ({}) => {
    const [listBlueprints, blueprintList, loading, error] = useBlueprintList()

    /*   useEffect(() => {
           const abortController = new AbortController();
           listBlueprints(abortController)
           return () => {
               abortController.abort();
           }
       },[]);

       if (error) return `Error`
       if (blueprintList == null || loading) return "Loading..."*/

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Blueprints"
                description={'Tryout our predefined workflows, proudly brought to you by the hamal.io team.'}
                actions={[]}
            />
            <ul className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {/*    {blueprintList.blueprints.map((item) => (
                BlueprintCard(item)
                         ))}*/}

                <BlueprintCard id={"1234"} name={"Telegram Money Service"}
                               description={"Be the first to know when your bank account hits a billion."}>

                </BlueprintCard>

                <BlueprintCard id={"1234"} name={"Email Crypto Bot"}
                               description={"Generates passive income every minute."}>

                </BlueprintCard>

                <BlueprintCard id={"1234"} name={"Telegram Spam Service"}
                               description={"Be the first to know when your bank account hits a billion."}>

                </BlueprintCard>

                <BlueprintCard id={"1234"} name={"Email Spam Bot"}
                               description={"Generates passive income every minute."}>

                </BlueprintCard>

                <BlueprintCard id={"1234"} name={"Telegram Spam Service"}
                               description={"Be the first to know when your bank account hits a billion."}>

                </BlueprintCard>

                <BlueprintCard id={"1234"} name={"Email Spam Bot"}
                               description={"Generates passive income every minute."}>

                </BlueprintCard>

            </ul>
        </div>
    );
}

const BlueprintCard = (item: BlueprintListItem) => {

    return (<Card
        key={item.id}
        className="relative overfunc-hidden duration-500 hover:border-primary/50 group"
        onClick={() => {
            console.log("Not implemented")
            //navigate(`/blueprints/${item.id}`)
        }}
    >
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle>{item.name}</CardTitle>
        </CardHeader>
        <CardContent>
            <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                <div className="flex justify-between py-3 gap-x-4">
                    {item.description}
                </div>
            </dl>
        </CardContent>
    </Card>)

}


export default BlueprintListPage
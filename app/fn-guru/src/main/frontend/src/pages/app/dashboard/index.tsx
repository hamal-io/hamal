import React, {useEffect} from 'react'
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs.tsx";
import Overview from "@/pages/app/dashboard/components/overview.tsx";
import FeatureTab from "@/pages/app/dashboard/components/feature.tsx";
import {useNamespaceGet} from "@/hook";
import {useUiState} from "@/hook/ui-state.ts";
import {RenameForm} from "@/pages/app/dashboard/components/feature-components/rename-form.tsx";


const DashboardPage: React.FC = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    if (loading) return "Loading"
    if (error) return "Error"
    return (
        <div className="flex-col md:flex">
            <div className="flex-1 space-y-4 p-8 pt-6">
                <div className="flex items-center justify-between space-y-2">
                    <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
                </div>
                {namespace && <RenameForm namespace={namespace}/>}

                <Tabs defaultValue="overview" className="space-y-4">
                    <TabsList>
                        <TabsTrigger value="overview">Overview</TabsTrigger>
                        <TabsTrigger value="analytics" disabled>
                            Analytics
                        </TabsTrigger>
                        <TabsTrigger value="reports" disabled>
                            Reports
                        </TabsTrigger>
                        <TabsTrigger value="notifications" disabled>
                            Notifications
                        </TabsTrigger>
                        <TabsTrigger value={"features"}>
                            Features
                        </TabsTrigger>
                    </TabsList>
                    <TabsContent value="overview" className="space-y-4">
                        <Overview/>
                    </TabsContent>
                    <TabsContent value={"features"}>
                        <FeatureTab namespace={namespace}/>
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    )
}

export default DashboardPage


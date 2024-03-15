import React from 'react'
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs.tsx";
import Overview from "@/pages/app/dashboard/components/overview.tsx";
import NamespaceDetailPage from "@/pages/app/dashboard/components/namespace/namespace.tsx";

const DashboardPage: React.FC = () => {
    return (
        <>
            <div className="flex-col md:flex">
                <div className="flex-1 space-y-4 p-8 pt-6">
                    <div className="flex items-center justify-between space-y-2">
                        <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
                    </div>
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
                            <TabsTrigger value={"namespace"}>
                                Namespace
                            </TabsTrigger>
                        </TabsList>
                        <TabsContent value="overview" className="space-y-4">
                            <Overview/>
                        </TabsContent>
                        <TabsContent value={"namespace"}>
                            <NamespaceDetailPage/>
                        </TabsContent>
                    </Tabs>
                </div>
            </div>
        </>
    );
}

export default DashboardPage


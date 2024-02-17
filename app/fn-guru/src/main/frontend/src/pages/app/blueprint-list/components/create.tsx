import {useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useAuth} from "@/hook/auth.ts";
import {useBlueprintCreate} from "@/hook/blueprint.ts";
import * as z from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Loader2, Plus} from "lucide-react";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Textarea} from "@/components/ui/textarea.tsx";
import {Input} from "@/components/ui/input.tsx";

const formSchema = z.object({
    name: z.string().max(128),
    description: z.string().max(512).optional(),
})

const CreateBlueprint = () => {
    const [auth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [isLoading, setLoading] = useState(false)
    const [createBlueprint, submitted] = useBlueprintCreate()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "anonymous" //test
        }
    })

    async function onSubmit() {
        setLoading(true)
        try {
            const {name, description} = form.getValues()
            createBlueprint(name, "xxx", description);
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
            setOpenDialog(false)
            form.reset()
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
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button variant="secondary" size="lg">
                        <Plus className="w-4 h-4 mr-1"/>
                        Create Blueprint
                    </Button>
                </DialogTrigger>
                <DialogContent>
                    <DialogHeader>New Blueprint:</DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name:</FormLabel>
                                        <FormControl>
                                            <Input
                                                placeholder={"Blueprint One"}
                                                {...field} />
                                        </FormControl>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="description"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Description</FormLabel>
                                        <FormControl>
                                            <Textarea
                                                placeholder={""}
                                                {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <div className="flex flex-row justify-between items-center">
                                <Button onClick={() => {
                                    form.reset()
                                    setOpenDialog(false)
                                }} variant="secondary">
                                    Cancel
                                </Button>
                                <Button type="submit">
                                    {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                    Submit
                                </Button>
                            </div>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )

}

export default CreateBlueprint
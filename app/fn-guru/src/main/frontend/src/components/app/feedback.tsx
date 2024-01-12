import React, {useState} from "react";
import * as z from "zod";
import {FeedbackMood} from "@/types/feedback.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useFeedbackCreate} from "@/hook/feedback.ts";
import {useAuth} from "@/hook/auth.ts";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Input} from "@/components/ui/input.tsx";
import {Loader2} from "lucide-react";
import {Checkbox} from "@/components/ui/checkbox.tsx";


const formSchema = z.object({
    message: z.string().max(4096),
})

const Feedback = () => {
    const [auth] = useAuth()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [isLoading, setLoading] = useState(false)
    const [createFeedback, submitted] = useFeedbackCreate()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            message: ""
        },
    })

    async function onSubmit(message: string, mood: FeedbackMood) {
        setLoading(true)
        try {
            createFeedback(message, mood, auth.accountId)
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
            setOpenDialog(false)
            form.reset()
        }

    }

    return (
        <div style={{
            position: 'fixed',
            top: '50%',
            right: 0,
            transform: 'translateY(-50%) rotate(90deg)',
            cursor: 'pointer',
        }}>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button variant="secondary" size="lg">
                        Feedback
                    </Button>
                </DialogTrigger>
                <DialogContent>
                    <DialogHeader>Feedback:</DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="message"
                                render={({field}) => (
                                    <>
                                        <FormItem>
                                            <FormLabel>How do you feel?</FormLabel>
                                            <FormControl>
                                                <>
                                                    <Checkbox>Happy</Checkbox>
                                                    <Checkbox>Angry</Checkbox>
                                                </>
                                            </FormControl>

                                        </FormItem>
                                        <FormItem>
                                            <FormLabel>Leave us a message</FormLabel>
                                            <FormControl>
                                                <Input
                                                    placeholder="How is your experience so far? What features do you miss?" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    </>
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
        </div>
    )
}

export default Feedback
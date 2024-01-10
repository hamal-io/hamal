import React, {useState} from "react";
import * as z from "zod";
import {FeedbackMood} from "@/types/feedback.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Form} from "@/components/ui/form.tsx";
import {useFeedbackCreate} from "@/hook/feedback.ts";

const Feedback = () => {
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [isLoading, setLoading] = useState(false)
    const [create, submitted] = useFeedbackCreate()

    async function onSubmit(message: string, mood: FeedbackMood) {
        setLoading(true)
        try {
            create(message, mood)
        } catch (e) {
            console.error(e)
        }
        setLoading(false)
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
                    <DialogHeader>Leave Feedback</DialogHeader>
                    <Button type="submit"></Button>

                </DialogContent>
            </Dialog>
        </div>
    )
}

export default Feedback
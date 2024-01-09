import {useState} from "react";
import * as z from "zod";
import {FeedbackMood} from "@/types/feedback.ts";

const formSchema = z.object({
    name: z.string(),
})

const Feedback = () => {
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    async function onSubmit(message: z.infer<typeof formSchema>, mood: FeedbackMood)

    //TODO-142
}
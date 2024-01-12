export const enum FeedbackMood {
    Happy = 0,
    Excited,
    Normal,
    Overwhelmed,
    Disappointed,
    Angry
}

export interface Feedback {
    mood: FeedbackMood;
    message: string
}

export interface FeedbackCreateSubmitted {
    id: string;
    status: string;
}
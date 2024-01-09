export enum FeedbackMood {
    Happy,
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
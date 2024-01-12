export interface FeedbackMood {
    [key: string]: {
        value: number;
        label: string;
    }
}

export const FeedbackMoods: FeedbackMood = {
    Happy: { value: 0, label: "Happy" },
    Excited: { value: 1, label: "Excited" },
    Normal: { value: 2, label: "Normal" },
    Overwhelmed: { value: 3, label: "Overwhelmed" },
    Disappointed: { value: 4, label: "Disappointed" },
    Angry: { value: 5, label: "Angry" }
};

export interface Feedback {
    mood: FeedbackMood;
    message: string
}

export interface FeedbackCreateSubmitted {
    id: string;
    status: string;
}
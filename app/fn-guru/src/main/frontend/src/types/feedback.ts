export interface FeedbackMood {
    [key: string]: {
        value: number;
        label: string;
        emoji: string
    }
}

export const FeedbackMoods: FeedbackMood = {
    Happy: {value: 0, label: "Happy", emoji: '😁'},
    Excited: {value: 1, label: "Excited", emoji: '😍'},
    Normal: {value: 2, label: "Normal", emoji: '🙂'},
    Overwhelmed: {value: 3, label: "Overwhelmed", emoji: '🫣'},
    Disappointed: {value: 4, label: "Disappointed", emoji: '🥱'},
    Angry: {value: 5, label: "Angry", emoji: '😡'}
};

export interface FeedbackCreateSubmitted {
    id: string;
    status: string;
}
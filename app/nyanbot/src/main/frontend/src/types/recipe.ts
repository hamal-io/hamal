import {Tag} from "@/pages/app/recipe-list/components/tags.ts";

interface Recipe {
    id: string;
    name: string;
    description: string;
    tags: Array<Tag>
}


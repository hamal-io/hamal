import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,} from "@/components/ui/dialog"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"

const Deploy = () => {
    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button variant="secondary">Deploy</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[800px]">
                <DialogHeader>
                    <DialogTitle>Deploy Function</DialogTitle>
                    <DialogDescription>
                        This will deploy the latest saved version of your function.
                    </DialogDescription>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                    <div className="grid gap-2">
                        <Label htmlFor="name">Name</Label>
                        <Input id="name" autoFocus/>
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="description">Description</Label>
                        <Input id="description"/>
                    </div>
                </div>
                <DialogFooter>
                    <Button type="submit">Deploy</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

export default Deploy;
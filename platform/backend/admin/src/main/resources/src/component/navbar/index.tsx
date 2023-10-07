import {Navbar as Delegate} from "flowbite-react";
import {useNavigate} from "react-router-dom";
import {FC} from "react";

const l = {
    base: "block py-2 pr-4 pl-3 md:p-0 text-xl",
    active: {
        on: "bg-green-700 text-white dark:text-white md:bg-transparent md:text-cyan-700",
        off: "border-b border-gray-100 text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white"
    },
    disabled: {
        on: "text-gray-400 hover:cursor-not-allowed dark:text-gray-600",
        off: ""
    }
};

export const Navbar: FC = () => {
    const navigate = useNavigate()
    return (
        <Delegate
            theme={{
                base: "bg-white px-2 py-5",
                rounded: {
                    on: "rounded",
                    off: ""
                },
                bordered: {
                    on: "border",
                    off: ""
                },
                inner: {
                    base: "mx-auto flex flex-wrap items-center justify-between",
                    fluid: {
                        on: "",
                        off: "container"
                    }
                }
            }}
        >
            <Delegate.Toggle/>
            <Delegate.Collapse>
                <Delegate.Link onClick={_ => navigate("/", {replace: true})} theme={l}>
                    Dashboard
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/adhoc", {replace: true})} theme={l}>
                    Adhoc
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/executions", {replace: true})} theme={l}>
                    Execution
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/logs", {replace: true})} theme={l}>
                    Logs
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/functions", {replace: true})} theme={l}>
                    Function
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/triggers", {replace: true})} theme={l}>
                    Trigger
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/groups", {replace: true})} theme={l}>
                    Group
                </Delegate.Link>
                <Delegate.Link onClick={_ => navigate("/accounts", {replace: true})} theme={l}>
                    Account
                </Delegate.Link>
            </Delegate.Collapse>
        </Delegate>
    )
}
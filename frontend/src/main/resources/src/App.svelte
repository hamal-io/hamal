<script lang="ts">
    import {onMount} from "svelte";
    import {executions} from "@/store";

    let adhocScript = "local log = require('log')\nlog.info({1,2,3})"

    onMount(getExecutions);

    async function getExecutions(){
        fetch("http://localhost:8084/v1/executions?limit=100")
            .then(response => response.json())
            .then(data => {
                console.log(data.executions);
                executions.set(data.executions);
            }).catch(error => {
            console.log(error);
            return [];
        });
    }

    function adhoc() {
        console.log("adhoc execution")
        fetch("http://localhost:8084/v1/adhoc",{
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/text'
            },
            method: "POST",
            body: adhocScript
        })
            .then(response => getExecutions())
            .catch(error => {
            console.log(error);
            return [];
        });
    }

</script>




<textarea  bind:value={adhocScript} id="message" rows="4" class="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Write your thoughts here..."/>
<button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        on:click={adhoc}
>
    Execute
</button>

<h1> Executions</h1>
{#each $executions as execution}
    <div>
        <h2> {execution.id}</h2>
        <h3> {execution.ref}</h3>
        <h4 style="color: green"> {execution.state} </h4>
    </div>
{/each}


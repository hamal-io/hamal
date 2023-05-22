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

<style>
    h1 {
        color: rebeccapurple;
    }
</style>


<h1> Executions</h1>

<button on:click={adhoc}>Execute</button>
<textarea rows="10" bind:value={adhocScript} style="width: 50%"/>

{#each $executions as execution}
    <div>
        <h2> {execution.id}</h2>
        <h3> {execution.ref}</h3>
        <h4 style="color: green"> {execution.state} </h4>
    </div>
{/each}


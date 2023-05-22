<script lang="ts">
    import {onMount} from "svelte";
    import {executions} from "@/store";

    onMount(async () => {
        fetch("http://localhost:8084/v1/executions?limit=2")
            .then(response => response.json())
            .then(data => {
                console.log(data.executions);
                executions.set(data.executions);
            }).catch(error => {
            console.log(error);
            return [];
        });
    });
</script>

<style>
    h1 {
        color: rebeccapurple;
    }
</style>

<h1> Executions</h1>

{#each $executions as execution}
    <div>
        <h2> {execution.id}</h2>
        <h3> {execution.ref}</h3>
        <h4 style="color: green"> {execution.state} </h4>
    </div>
{/each}


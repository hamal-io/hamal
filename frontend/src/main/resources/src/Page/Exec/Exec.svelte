<script lang="ts">
    import {execs} from "./store";
    import {onMount} from 'svelte';

    onMount(getFuncs);

    async function getFuncs() {
        fetch("http://localhost:8084/v1/execs")
            .then(response => response.json())
            .then(data => {
                console.log(data.execs);
                execs.set(data.execs);
            }).catch(error => {
            console.log(error);
            return [];
        });
    }

    function save() {

    }

</script>

<div class="w-full">
    <h1> Execs</h1>
    {#each $execs as func}
        <div>
            <h2> {func.id}</h2>
            <h4> {func.name} </h4>
        </div>
    {/each}
</div>
<script lang="ts">
    import {triggers} from "./store";
    import {onMount} from 'svelte';

    onMount(getTriggers);

    let name = 'some-trigger-name'
    let funcId = ''

    async function getTriggers() {
        fetch("http://localhost:8084/v1/triggers")
            .then(response => response.json())
            .then(data => {
                console.log(data.triggers);
                triggers.set(data.triggers);
            }).catch(error => {
            console.log(error);
            return [];
        });
    }

    function save() {
        console.log("adhoc trigger")
        fetch("http://localhost:8084/v1/triggers", {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify({
                    name,
                    funcId,
                    type: 'FixedRate',
                    duration: "PT1S"
                }
            )
        })
            .then(response => getTriggers())
            .catch(error => {
                console.log(error);
                return [];
            });
    }

</script>

<div class="w-full">
    <div class="columns-2">

        <input bind:value={name} style="background: red"/>
        <input bind:value={funcId} style="background: red"/>

        <button
                class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                on:click={save}
        >
            Save
        </button>

        <div class="w-1/2">
            <h1> Triggers</h1>
            {#each $triggers as trigger}
                <div>
                    <h2> {trigger.id}</h2>
                    <h4> {trigger.name} </h4>
                </div>
            {/each}
        </div>
    </div>
</div>
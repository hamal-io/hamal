<script lang="ts">
    import type {ApiTrigger} from "./store";
    import {triggers} from "./store";
    import {onMount} from 'svelte';

    onMount(getTriggers);

    let name = 'some-trigger-name'
    let funcId = ''
    let fixedRate = 'PT5S'
    let topicId = null

    async function getTriggers() {
        fetch("http://localhost:8008/v1/triggers")
            .then(response => response.json<ApiTrigger>())
            .then(data => {
                triggers.set(data.triggers);
            }).catch(error => {
            console.log(error);
            return [];
        });
    }

    function save() {
        console.log("adhoc trigger")
        fetch("http://localhost:8008/v1/triggers", {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify({
                    name,
                    funcId,
                    // type: 'FixedRate',
                    // correlationId: 'test',
                    // inputs: {},
                    // duration: fixedRate
                    type: 'Event',
                    correlationId: 'test',
                    inputs: {},
                    topicId: topicId
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

        <div class="flex-box w-1/2">
            <div class="flex-content py-2">
                <label for="name">name</label>
                <input bind:value={name} id="name" style="background: red"/>

            </div>

            <div class="flex-content py-2">
                <label for="func-id">func id</label>
                <input bind:value={funcId} id="func-id" style="background: red"/>
            </div>

            <div class="flex-content py-2">
                <label for="fixed-rate">fixed rate</label>
                <input bind:value={fixedRate} id="fixed-rate" style="background: red"/>
            </div>

            <div class="flex-content py-2">
                <label for="fixed-rate">topic id</label>
                <input bind:value={topicId} id="topic-id" style="background: red"/>
            </div>

            <div class="flex-content py-2">
                <button
                        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                        on:click={save}
                >
                    Save
                </button>
            </div>

        </div>

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
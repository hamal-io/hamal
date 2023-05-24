<script lang="ts">
    import {triggers} from "./store";
    import type monaco from 'monaco-editor';
    import {onMount} from 'svelte';
    import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';
    import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker';
    import cssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker';
    import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker';
    import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker';

    let divEl: HTMLDivElement = null;
    let editor: monaco.editor.IStandaloneCodeEditor;
    let Monaco;


    onMount(async () => {
        // @ts-ignore
        self.MonacoEnvironment = {
            getWorker: function (_moduleId: any, label: string) {
                if (label === 'json') {
                    return new jsonWorker();
                }
                if (label === 'css' || label === 'scss' || label === 'less') {
                    return new cssWorker();
                }
                if (label === 'html' || label === 'handlebars' || label === 'razor') {
                    return new htmlWorker();
                }
                if (label === 'typescript' || label === 'javascript') {
                    return new tsWorker();
                }
                return new editorWorker();
            }
        };

        Monaco = await import('monaco-editor');
        editor = Monaco.editor.create(divEl, {
            value: [
                "local log = require('log')",
                "log.info('trigger invoked')"
            ].join("\n"),
            language: 'lua',
            lineNumbers: "on",
            scrollBeyondLastLine: false,
            readOnly: false,
        });


        return () => {
            editor.dispose();
        };
    });

    onMount(getTriggers);

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
                    name: "trigger-name",
                    code: editor.getValue()
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
        <button
                class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                on:click={save}
        >
            Save
        </button>

        <div bind:this={divEl} class="h-screen w-full"/>

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
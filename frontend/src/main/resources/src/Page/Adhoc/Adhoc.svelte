<script lang="ts">
    import {execs} from "./store";
    import type monaco from 'monaco-editor';
    import { onMount } from 'svelte';
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
                "log.info('automate the world')",
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

    onMount(getExecutions);

    async function getExecutions() {
        fetch("http://localhost:8084/v1/execs?limit=100")
            .then(response => response.json())
            .then(data => {
                execs.set(data.execs);
            }).catch(error => {
            console.log(error);
            return [];
        });
    }

    function adhoc() {
        console.log("adhoc execution")
        fetch("http://localhost:8084/v1/adhoc", {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/text'
            },
            method: "POST",
            body: editor.getValue()
        })
            .then(response => getExecutions())
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
                on:click={adhoc}
        >
            Execute
        </button>

        <div bind:this={divEl} class="h-screen w-full" />

        <div class="w-1/2">
            <h1> Executions</h1>
            {#each $execs as execution}
                <div>
                    <h2> {execution.id}</h2>
                    <h3> {execution.ref}</h3>
                    <h4 style="color: green"> {execution.state} </h4>
                </div>
            {/each}
        </div>
    </div>
</div>
<script>
    import ContactCard from "./ContactCard.svelte";

    export let name;
    let age = 0;
    let uppercaseName;
    $: uppercaseName = name.toUpperCase()

    $: console.log(name)


    function incrementAge() {
        age += 1
    }

    function changeName(evt) {
        name = evt.target.value
    }

</script>

<style>
    h1 {
        color: #ea03ea;
    }
</style>

<h1>Hello {uppercaseName}! - {age}</h1>
<button on:click={incrementAge}> Increment Age</button>
<!--<button on:click={changeName}> Change Name</button>-->
<!--<input type="text" value={name} on:input={changeName}>-->
<input type="text" bind:value={name}>
<ContactCard
        {name}
/>
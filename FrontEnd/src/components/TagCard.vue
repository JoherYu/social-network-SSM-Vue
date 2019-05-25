<template>
<router-link class="badge badge-light" :to="'/main/tag/' + tag.id">
{{ tag.name }} {{ tag.photoCount }}
</router-link>   
</template>

<script>
    export default {
        name: "TagCard",
        props: {
          tag:Object
        }
    }
</script>

<style scoped>

</style>

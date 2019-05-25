<template>
    <div class="photo-card card">
        <router-link class="card-thumbnail" :to="'/main/photo/' + photo.id">
            <img class="card-img-top portrait" :src="photo.filenameS">
        </router-link>
        <div class="card-body">
            <span class="oi oi-star"></span> {{ photo.collectorCount }}
            <span class="oi oi-comment-square"></span> {{ photo.commentCount }}
        </div>
    </div>    
</template>

<script>
    export default {
        name: "PhotoCard",
        props: {
          photo:Object
        }
    }
</script>

<style scoped>

</style>

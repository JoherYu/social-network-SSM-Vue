<template>
     <div class="user-card text-center">
        <router-link :to="'/main/user/' + user.username">
            <img class="rounded avatar-m" :src="user.avatarM">
        </router-link>
        <h6>
            <router-link :to="'/main/user/' + user.username">{{ user.username }}</router-link>
        </h6>
        <follow-area :user='user'></follow-area>
    </div>   
</template>

<script>
    import FollowArea from './FollowArea'

    export default {
        name: "UserCard",
      components: {FollowArea},
      props: ['user'],
        comments:{
          FollowArea
        }
    }
</script>

<style scoped>

</style>

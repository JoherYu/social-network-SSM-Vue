<template>
    <div v-if="$store.state.Nav.HasLogin">
        <div v-if="user.username != $store.state.Nav.username">
            <div v-if="user.followed">
                <form class="inline">
                    <button type="button" class="btn btn-dark btn-sm" @click="unfollow">取消关注</button>
                    <div v-if="user.following">
                        <p class="badge badge-light">互相关注中</p>
                    </div>
                </form>
            </div>
            <div v-else>
                <form class="inline">
                    <button type="button" class="btn btn-primary btn-sm" @click="follow">关注</button>
                    <div v-if="user.following">
                        <p class="badge badge-light">对方已关注你</p>
                    </div>
                </form>
            </div>
        </div>
    </div>    
    <div v-else>
        <form class="inline">
            <button type="button" class="btn btn-primary btn-sm" @click="redirectLogin">关注</button>
        </form>
    </div>
</template>

<script>
    import axios from 'axios'

    export default {
        name: "FollowArea",
        props: ['user'],
        methods:{
            followFlagChange(){
                this.user.followed = !this.user.followed
            },
            unfollow(){
                axios.delete('/follow/' + this.user.username,{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', {
                    message:res.message,
                    type:res.type
                })
                this.followFlagChange()
            })
            },
            follow(){
                axios.post('/follow/' + this.user.username, {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', {
                    message:res.message,
                    type:res.type
                })
                this.followFlagChange()
            })
            },
            redirectLogin(){
                this.$store.commit('UpdateRedirectPage', this.$route.path)
                this.$store.commit('FlashMessage', {message:'请先登录', type:"warning"})
                this.$router.push('/')
            }      
        }
    }
</script>

<style scoped>

</style>

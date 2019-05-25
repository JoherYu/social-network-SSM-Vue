<template>
  <div class="popup-card">
    <img class="rounded img-fluid avatar-s popup-avatar" :src="user.avatarM">
    <div class="popup-profile">
      <h6>{{ user.name }}</h6>
      <p class="text-muted">{{ user.username }}
        <span v-if="$store.state.Nav.HasLogin">
          <span v-if="$store.state.Nav.username != user.username && user.following">
            <span v-if="user.followed">
            <span class="badge badge-light">互相关注中</span>
            </span>
            <span v-else>
            <span class="badge badge-light">对方正在关注你</span>
            </span>
          </span>
        </span>
      </p>
    </div>
    <p class="card-text">
      <router-link :to="'/main/user/' + user.username">
        <strong>{{ user.photoCount }}</strong> 张相片
      </router-link>&nbsp;
      <router-link :to="'/main/follower/' + user.username">
        <strong :id="'followers-count-' + user.id">
          {{ user.followerCount - 1 }}
        </strong> 个粉丝
      </router-link>
    </p>
    <router-link :to="'/main/user/' + user.username" class="btn btn-light btn-sm button">主页</router-link>
    <div v-if="$store.state.Nav.HasLogin">
      <div v-if="$store.state.Nav.username != user.username">
      <button @click="unfollow(user)"
              :class="{ hide: !user.followed }" class="btn btn-dark btn-sm unfollow-btn">
        取消关注
      </button>
      <button @click="follow(user)"
              :class="{ hide: user.followed }" class="btn btn-primary btn-sm follow-btn">
        关注
      </button>
      </div>
    </div>
    <div v-else>
      <form class="inline" @click="redirectLogin">
          <a href="javascript:void(0)">
              <el-button  type="button" size="small" class="btn btn-primary btn-sm">
                  <span class="oi oi-star"></span> 关注
              </el-button>
          </a>
      </form>      
    </div>
  </div>
</template>

<script>
    import axios from 'axios'

    export default {
        name: "PopupCard",
        props:['user'],
        methods:{
          unfollow(user){
            axios.delete('/follow/' + user.username, {
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
              }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              user.follower_count = user.follower_count - 1
              user.you_are_following_user = !user.you_are_following_user
            })
          },
          follow(user){
            axios.post('/follow/' + user.username, {},{
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
              }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              user.follower_count = user.follower_count + 1
              user.you_are_following_user = !user.you_are_following_user
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
.button {
  margin-bottom: 5px
}
</style>

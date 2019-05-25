<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand" to="/admin/manage">
        <img src="./../assets/favicon.ico" alt="Albumy"> 分相管理面板
      </router-link>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01"
              aria-controls="navbarColor01" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarColor01">
        <div class="navbar-nav ml-auto">
          <li  class="nav-item"><router-link to="/" class='nav-link'>返回分相</router-link></li>
          <div class="dropdown nav-item">
            <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown" role="button"
               aria-expanded="false">管理<span class="caret"></span>
            </a>
            <div class="dropdown-menu dropdown-menu-right" role="menu">
              <router-link class="dropdown-item" to="/admin/manage/photo">相片</router-link>
              <router-link class="dropdown-item" to="/admin/manage/user">用户</router-link>
              <router-link class="dropdown-item" to="/admin/manage/tag">标签</router-link>
              <router-link class="dropdown-item" to="/admin/manage/comment">评论</router-link>
            </div>
          </div>
          <div class="dropdown nav-item">
            <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown" role="button"
               aria-expanded="false">
              <img class="avatar-xs"
                   :src="$store.state.Nav.AvatarPathS">
              <span class="caret"></span>
            </a>
            <div class="dropdown-menu dropdown-menu-right" role="menu">
              <h6 class="dropdown-header">用户 {{ $store.state.Nav.username }} 已登录</h6>
              <router-link class="dropdown-item" :to="'/main/user/' + $store.state.Nav.username">
                <span class="oi oi-person"></span> 我的主页
              </router-link>
              <div class="dropdown-divider"></div>
              <router-link class="dropdown-item" to="/main/setting">
                <span class="oi oi-cog"></span> 设置
              </router-link>
              <a class="dropdown-item" href="javascript:void(0)" @click="logout">
                <span class="oi oi-power-standby"></span> 退出
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
    import axios from 'axios';

    export default {
        name: "AdminNav",
        methods:{
          logout(){
            axios.delete('/session',{
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
              }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              sessionStorage.clear()
              localStorage.clear()
              this.$store.commit('Reset')
              this.$router.push('/')
            })
          },
        }
    }
</script>

<style scoped>

</style>

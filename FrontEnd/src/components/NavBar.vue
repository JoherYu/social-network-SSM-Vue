<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light modify">
    <div class="container">
      <a class="navbar-brand" href="/">
        <img src="./../assets/favicon.ico" alt="Albumy">
      </a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01"
              aria-controls="navbarColor01" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarColor01">
        <div class="navbar-nav mr-auto">
          <li  class="nav-item" v-bind:class="{ active: this.$route.path == '/main/home/*' }"><a href="javascript:void(0)" @click="RedirectVerification" class='nav-link'>主页</a></li>
          <li  class="nav-item" v-bind:class="{ active: this.$route.path == '/main/explore' }"><router-link to="/main/explore" class='nav-link'>发现</router-link></li>
          <form class="form-inline my-2 my-lg-0">
            <input type="text" v-model="q" style="width: 226px" class="form-control mr-sm-1" placeholder="图片、标签或用户名关键词" required>
            <button class="btn btn-light my-2 my-sm-0" type="button" @click="ToSearch">
                <span class="oi oi-magnifying-glass"></span>
            </button>
          </form>
        </div>
        <div>
          <div class="navbar-nav ml-auto" v-if="$store.state.Nav.HasLogin">
            <router-link class="nav-item nav-link" to="/main/notifications">
              <span class="oi oi-bell"></span>
              <span id="notification-badge" v-bind:class="{ hide: $store.state.Nav.NotificationCount == 0 }"
                    class="badge badge-danger badge-notification">{{ $store.state.Nav.NotificationCount }}</span>
            </router-link>
            <router-link class="nav-item nav-link" to="/main/upload" title="上传">
              <span class="oi oi-cloud-upload"></span>&nbsp;&nbsp;
            </router-link>
            <div class="dropdown nav-item">
              <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown" role="button"
                aria-expanded="false">
                <img class="avatar-xs" v-bind:src="$store.state.Nav.AvatarPathS" />
                <span class="caret"></span>
              </a>
              <div class="dropdown-menu dropdown-menu-right" role="menu">
                <h6 class="dropdown-header">用户{{ $store.state.Nav.username }}已登录</h6>
                <router-link class="dropdown-item" :to="'/main/user/' + $store.state.Nav.username">
                  <span class="oi oi-person"></span> 我的主页
                </router-link>
                <div class="dropdown-divider"></div>
                <router-link class="dropdown-item" to="/main/setting">
                  <span class="oi oi-cog"></span> 设置
                </router-link>
                <div v-if="$store.state.Nav.CanModerate">
                <router-link class="dropdown-item" to="/admin/manage">
                  <span class="oi oi-dashboard"></span> 管理
                </router-link>
                </div>
                <a class="dropdown-item" href="javascript:void(0)" @click="logout">
                  <span class="oi oi-power-standby"></span> 注销
                </a>
              </div>
            </div>
          </div>
          <div class="navbar-nav ml-auto" v-else>
            <router-link class="btn btn-outline-primary" to="/" @click.native="ToLogin">登录</router-link>&nbsp;&nbsp;
            <router-link class="btn btn-primary" to="/" @click.native="ToRegister">注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
    import axios from 'axios'
    export default {
        name: "NavBar",
        data(){
          return{
            q:''
          }
        },
        methods:{
          logout(){
            axios.delete('/session',{
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
              }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              if(res.type == "success"){
              sessionStorage.clear()
              localStorage.clear()
              this.$store.commit('Reset')
              this.$router.push('/')
              }

            })
          },
          ToLogin(){
            this.$store.commit('ChangeIndexFlag', 'login')
          },
          ToRegister(){
            this.$store.commit('ChangeIndexFlag', 'register')
          },
          RedirectVerification(){
            if (this.$store.state.Nav.HasLogin){
              this.$router.push('/main/home')
            }else {
              this.$router.push('/')
              this.$store.commit('ChangeIndexFlag', 'login')
            }
          },
          ToSearch(){
            this.$store.commit('SendQueryParam', this.q)
            if (this.$store.state.Nav.query == ''){
                this.$store.commit('FlashMessage', {
                    message: '请输入相片、用户名或标签关键词',
                    type: 'warning'
                    })
                this.$store.commit('SendSearchData', {items:{}, count:0 ,current_page:1, per_page:20})  
              }else{
            this.$router.push('/main/search')
            axios.get('/search/' + this.$store.state.Page.SearchCategory + '/' + this.$store.state.Nav.query, {
            }).then((result)=>{
            var res = result.data
            if (res.message) {
                this.$store.commit('FlashMessage', {
                message: res.message,
                type: res.type
                })
            } else {
              this.$store.commit('SendSearchData', res)
              this.$store.commit('SearchLoadingChange')
            }
        })
              }
          }
        }
    }
</script>



<style scoped>
.modify{margin-bottom:10px} 
</style>

<template>
  <div>
    <admin-nav></admin-nav>
    <div class="container" id="top">
      <flash-message></flash-message>
      <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
              <el-breadcrumb separator-class="el-icon-arrow-right">
              <el-breadcrumb-item :to="{ path: '/admin/manage' }">管理面板主页</el-breadcrumb-item>
              <el-breadcrumb-item>管理用户</el-breadcrumb-item>
              </el-breadcrumb>
          </ol>
      </nav>
      <div class="page-header">
          <h1>用户数
              <small class="text-muted">{{ pagination.count }}</small>
          </h1>
          <ul class="nav nav-pills">
              <li class="nav-item">
                  <a class="nav-link disabled" href="#">筛选 </a>
              </li>
              <li class="nav-item">
                  <a class="nav-link" :class="{ active : filter == 'all' }"
                    href="javascript:void(0)" @click="getData('all')">全部</a>
              </li>
              <li class="nav-item">
                  <a class="nav-link" :class="{ active : filter == 'locked' }"
                    href="javascript:void(0)" @click="getData('locked')">锁定</a>
              </li>
              <li class="nav-item">
                  <a class="nav-link" :class="{ active : filter == 'blocked' }"
                    href="javascript:void(0)" @click="getData('blocked')">封禁</a>
              </li>
              <li class="nav-item">
                  <a class="nav-link" :class="{ active : filter == 'administrator' }"
                    href="javascript:void(0)" @click="getData('administrator')">管理员</a>
              </li>
              <li class="nav-item">
                  <a class="nav-link" :class="{ active : filter == 'moderator' }"
                    href="javascript:void(0)" @click="getData('moderator')">协管员</a>
              </li>
          </ul>
      </div>
      <div v-if="user_count">
          <table class="table table-striped">
              <thead>
              <tr>
                  <th>头像</th>
                  <th>昵称/用户名</th>
                  <th>用户分类</th>
                  <th>个人简介</th>
                  <th>城市</th>
                  <th>注册日期</th>
                  <th>相片数</th>
                  <th>处理</th>
              </tr>
              </thead>
                <tr v-for="user in users" :key="user.id">
                    <td><img :src="user.avatarS"></td>
                    <td>{{ user.name }}<br>{{ user.username }}</td>
                    <td>{{ user.roleName }}</td>
                    <td>{{ user.bio }}</td>
                    <td>{{ user.location }}</td>
                    <td>{{ $moment(user.memberSince).format('LL') }}</td>
                    <td>
                        <router-link :to="'/main/user/' + user.username">{{ user.photoCount }}</router-link>
                    </td>
                    <td>
                        <span v-if="user.locked">
                            <form class="inline" @click="unlock(user.id)">
                                <input type="button" class="btn btn-secondary btn-sm" value="解锁">
                            </form>
                        </span>
                        <span v-else>
                            <form class="inline" @click="lock(user.id)">
                                <input type="button" class="btn btn-warning btn-sm" value="锁定">
                            </form>
                        </span>
                        <span v-if="user.active">
                            <form class="inline" @click="block(user.id)">
                                <input type="button" class="btn btn-warning btn-sm" value="封禁">
                            </form>
                        </span>
                        <span v-else>
                            <form class="inline" @click="unblock(user.id)">
                                <input type="button" class="btn btn-secondary btn-sm" value="解封">
                            </form>
                        </span>
                        <a class="btn btn-light btn-sm" :href="'mailto:' + user.email">发送邮件</a>
                        <span v-if="$store.state.Nav.IsAdmin">
                            <router-link class="btn btn-warning btn-sm" :to="'/main/setting/' + user.id">编辑他的资料</router-link>
                        </span>
                    </td>
                </tr>
          </table>
          <div class="page-footer"><pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination></div>
      </div> 
      <div v-else>
          <div class="tip"><h5>没有用户</h5></div>
      </div>
    </div>
  </div>
</template>

<script>
    import AdminNav from "../components/AdminNav"
    import FlashMessage from './../components/FlashMessage'
    import pagination from "../components/pagination"
    import axios from "axios"

    export default {
        name: "ManageUser",
        components:{
          AdminNav,
          pagination,
          FlashMessage
        },
        data(){
          return{
            page:1,
            pagination:{},
            filter:'all',
            user_count:0,
            users:[]
          }
        },
        mounted(){
          this.getData(this.filter)
        },
        methods:{
          getData(filter){
            if (this.$store.state.Page.ManageFilter == 'locked'){
              this.filter = 'locked'
              this.$store.commit('ChangeManageFilter', '')
            }else if(this.$store.state.Page.ManageFilter == 'blocked'){
              this.filter = 'blocked'
              this.$store.commit('ChangeManageFilter', '')
            }else{
              this.filter = filter
            }
            
            axios.get('/management/users', {
              params:{
                page:this.page,
                filterRule:this.filter
              }
            }).then((result)=>{
              var res = result.data
              this.users = res.users
              this.user_count = res.userCount
              this.pagination = res.pagination
            })
          },
            unlock(id){
                var id = id
                axios.patch('/user/' + id + '/unlock', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.getData(this.filter)
                })
            },
            lock(id){
                var id = id
                axios.patch('/user/' + id + '/lock', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.getData(this.filter)
                })
            },
            unblock(id){
                var id = id
                axios.patch('/user/' + id + '/unblock', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.getData(this.filter)
                })
            },
            block(id){
                var id = id
                axios.patch('/user/' + id + '/block', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.getData(this.filter)
                })
            },
            ChangePage(val){
                this.page = val
                this.getData(this.filter)
                document.getElementById("top").scrollIntoView();
            },
            PrePage(){
                this.page--
            },
            NextPage(){
                this.page++
            },
        }        
    }
</script>

<style scoped>

</style>

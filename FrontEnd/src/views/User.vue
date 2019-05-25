<template>
    <div>
        <nav-bar></nav-bar>
        <main>
            <flash-message></flash-message>
            <div class="container">
                <div class="row">
                    <div class="col-md-3">
                        <a @click="toEditAvatar" href="javascript:void(0)"
                        title="Change avatar">
                            <img class="img-fluid rounded" :src="user.avatarL">
                        </a>
                    </div>
                    <div class="col-md-9">
                        <h1>{{ user.name }}
                            <small class="text-muted">{{ user.username }}</small>
                        </h1>
                        <span v-if="user.bio"><p>{{ user.bio }}</p></span>
                        <p>
                            <span v-if="user.website">
                                <span class="oi oi-link-intact"></span>
                                <a :href="user.website" target="_blank">{{ user.website }}</a>&nbsp;&nbsp;
                            </span>
                            <span v-if="user.location">
                                <span class="oi oi-map-marker"></span>
                                <a :href="'https://www.google.com/maps?q=' + user.location"
                                target="_blank">{{ user.location }}</a>&nbsp;&nbsp;
                            </span>
                            <span class="oi oi-calendar"></span>
                            注册时间： {{ $moment(user.memberSince).format('LL') }}
                        </p>
                        <div>
                            <span v-if="$store.state.Nav.username != user.username">
                                <span v-if="$store.state.Nav.CanModerate">
                                    <span v-if="user.locked && lockDoor">
                                        <form class="inline" @click="unlock(user.id)">
                                            <input type="button" class="btn btn-secondary btn-sm" value="解锁">
                                        </form>
                                    </span>
                                    <span v-else>
                                        <form class="inline" @click="lock(user.id)">
                                            <input type="button" class="btn btn-warning btn-sm" value="锁定">
                                        </form>
                                    </span>
                                    <span v-if="user.active && blockDoor">
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
                                </span>
                                <span v-if="$store.state.Nav.IsAdmin">
                                    <router-link class="btn btn-warning btn-sm" :to="'/main/setting/' + user.id">编辑他的资料</router-link>
                                </span>
                            </span>
                            <div class="float-right">
                                <follow-area :user='user'></follow-area>
                                <span v-if="$store.state.Nav.HasLogin && $store.state.Nav.username == user.username">
                                    <router-link class="btn btn-outline-primary btn-sm" to="/main/setting">编辑个人资料</router-link>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="user-nav" id="tabs">
                    <el-tabs v-model="activeName" type="card" @tab-click="getData()">
                        <el-tab-pane label="相片" name="photos">
                            <div class="row">
                                <div class="col-md-12">
                                    <div v-if="user.photoCount">
                                        <div v-for="photo in photos" :key="photo.id">
                                            <photo-card :photo="photo"></photo-card>
                                        </div>
                                    </div>
                                    <div v-else>
                                        <div class="tip text-center">
                                            <h3>没有相片</h3>
                                            <div v-if="$store.state.Nav.username == user.username">
                                                <router-link class="btn btn-link" to="/main/upload">上传</router-link>
                                            </div>
                                            <div v-else>
                                                <router-link class="btn btn-link" to="/main/explore">发现</router-link>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-if="user.photoCount">
                                <div class="page-footer">
                                    <pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
                                </div>
                            </div>
                        </el-tab-pane>
                        <el-tab-pane label="收藏" name="collections">
                            <div class="row">
                                <div class="col-md-12">
                                    <div v-if="user.publicCollections || $store.state.Nav.username == user.username">
                                        <div v-if="user.collectionCount">
                                            <div v-for="collect in collects" :key="collect.id">
                                                <photo-card :photo="collect"></photo-card>
                                            </div>
                                        </div>
                                        <div v-else>
                                            <div class="tip">
                                                <h3>没有收藏</h3>
                                            </div>
                                        </div>
                                    </div>
                                    <div v-else>
                                        <div class="tip">
                                            <h3>该用户的收藏仅自己可见</h3>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-if="user.collectionCount">
                                <div class="page-footer">
                                    <pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
                                </div>
                            </div>
                        </el-tab-pane>
                        <el-tab-pane label="关注" name="followings">
                            <div class="row">
                                <div class="col-md-12">
                                    <div v-if="(user.followingCount - 1) >= 0">
                                        <div v-for="follow in follows" :key="follow.id">
                                            <div v-if="follow.username != user.username">
                                                <user-card :user="follow"></user-card>
                                            </div>
                                        </div>
                                    </div>
                                    <div v-else>
                                        <div class="tip">
                                            <h3>没有关注</h3>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-if="(user.followingCount - 1 ) >= 0">
                                <div class="page-footer">
                                    <pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
                                </div>
                            </div>
                        </el-tab-pane>
                        <el-tab-pane label="粉丝" name="followers">       
                            <div class="row">
                                <div class="col-md-12">
                                    <div v-if="(user.followerCount - 1) >= 0">
                                        <div v-for="follow in follows" :key="follow.id">
                                            <div v-if="follow.username != user.username">
                                                <user-card :user="follow"></user-card>
                                            </div>
                                        </div>
                                    </div>
                                    <div v-else>
                                        <div class="tip">
                                            <h3>没有粉丝</h3>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-if="(user.followersCount - 1) >= 0">
                                <div class="page-footer">
                                    <pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
                                </div>
                            </div>
                        </el-tab-pane>
                    </el-tabs>
                </div>                                        
                <footer-ultimate></footer-ultimate>
            </div>
        </main>
    </div>
</template>

<script>
    import NavBar from "./../components/NavBar";
    import FlashMessage from "./../components/FlashMessage";
    import FooterUltimate from "./../components/FooterUltimate";
    import axios from 'axios';
    import FollowArea from "./../components/FollowArea"
    import PhotoCard from "./../components/PhotoCard"
    import UserCard from "./../components/UserCard"
    import pagination from "./../components/pagination"

    export default {
        name: "User",
        components: {
            FooterUltimate, 
            FlashMessage, 
            NavBar,
            FollowArea,
            PhotoCard,
            UserCard,
            pagination
        },
        data(){
            return {
                activeName: 'photos',
                user:{},
                photos:[],
                pagination:{},
                collects:{},
                follows:{},
                page:1,
                blockDoor:true,
                lockDoor:true
            }
        },
        mounted(){
            this.getData()
        },
        methods:{
            getData(){
                if (this.$store.state.Page.RedirectPage == '/main/home'){
                    this.activeName = 'collections'
                    this.$store.commit('UpdateRedirectPage', '/')
                }
                if (this.$store.state.Page.RedirectPage == '/main/follower'){
                    this.activeName = 'followers'
                    this.$store.commit('UpdateRedirectPage', '/')
                }

                var url = '/user/' + this.$route.params.username + '/' + this.activeName
                
                axios.get(url, {
                    params: {
                        page: this.page
                    }
                }).then((result)=>{
                    var res = result.data
                    this.user = res.user
                    this.photos = res.photos
                    this.pagination = res.pagination
                    this.collects = res.collects
                    this.follows = res.follows
                    if (res.message.message){
                        this.$store.commit('FlashMessage', res.message)
                    }                   
                })
            },
            unlock(id){
                var id = id
                axios.patch('/user/' + id + '/unlock', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    this.lockDoor = !this.lockDoor
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                })
            },
            lock(id){
                var id = id
                axios.patch('/user/' + id + '/lock', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    this.lockDoor = !this.lockDoor
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                })
            },
            unblock(id){
                var id = id
                axios.patch('/user/' + id + '/unblock', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    this.blockDoor = !this.blockDoor
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                })
            },
            block(id){
                var id = id
                axios.patch('/user/' + id + '/block', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    this.blockDoor = !this.blockDoor
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                })
            },
            ChangePage(val){
            this.page = val
            this.getData()
            document.getElementById("tabs").scrollIntoView();
          },
          PrePage(){
            this.page--
          },
          NextPage(){
            this.page++
          },
          toEditAvatar(){
              if (this.$store.state.Nav.username == this.user.username){
                  this.$store.commit('ChangeEditAvaterFlag', 'changeAvatar')
                  this.$router.push('/main/setting')
              }
          }
        },
        watch: {
            '$route' (to, from) {
                this.getData()
            }
        }
    }
</script>

<style scoped>

</style>

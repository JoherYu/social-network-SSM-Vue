<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <div class="container" id="top">
        <flash-message></flash-message>
        <div class="page-header">
            <h1>消息中心</h1>
        </div>
        <div class="row">
            <div class="col-md-3">
                <div class="nav nav-pills flex-column" role="tablist" aria-orientation="vertical">
                    <a class="nav-item nav-link" :class="{ active : showFlag != 'unread'}"
                    href="javascript:void(0)" @click="getData('all')">
                        所有消息
                    </a>
                    <a class="nav-item nav-link" :class="{ active : showFlag == 'unread'}"
                    href="javascript:void(0)" @click="getData('unread')">
                        未读消息
                    </a>
                </div>
            </div>
            <div class="col-md-9">
                <div class="card bg-light w-100">
                    <div class="card-header">{{ notification_count }} 条未读消息
                        <div class="float-right">
                            <a class="btn btn-light btn-sm" href="javascript:void(0)" @click="toEditSetting">
                                <span class="oi oi-cog" aria-hidden="true"></span>设置
                            </a>
                            <form class="inline" @click="readAll">
                                <button type="button" class="btn btn-light btn-sm">
                                    <span class="oi oi-check" aria-hidden="true"></span>全部标记为已读
                                </button>
                            </form>
                        </div>
                    </div>
                    <div class="card-body">
                        <div v-if="notifications.length != 0">
                            <ul class="list-group">
                                <div v-for="notification in notifications" :key="notification.id">
                                    <li class="list-group-item">
                                        <span v-html="notification.message"></span>
                                        <span class="float-right">
                                            {{ $moment(notification.timestamp).fromNow(refresh=true) }}
                                            <span v-if="notification.isRead == false && notificationFlag">
                                                <form class="inline" @click="read(notification)">
                                                    <button type="button" class="btn btn-light btn-sm">
                                                        <span class="oi oi-check" aria-hidden="true"></span>
                                                    </button>
                                                </form>
                                            </span>
                                        </span>
                                    </li>
                                </div>
                            </ul><br>
                            <div class="text-right page-footer">
                                <pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
                            </div>
                        </div>
                        <div v-else>
                            <div class="tip text-center">
                                <h6>没有消息</h6>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <footer-ultimate></footer-ultimate>
      </div>
    </main>
  </div>    
</template>

<script>
    import NavBar from "../components/NavBar";
    import FlashMessage from "../components/FlashMessage";
    import FooterUltimate from "../components/FooterUltimate";
    import pagination from "../components/pagination";
    import axios from 'axios'

    export default {
        name: "Notification",
        components: {FooterUltimate, FlashMessage, NavBar, pagination},
        data(){
            return{
                showFlag:'all',
                page:1,
                pagination:{},
                notifications:[],
                notification_count:0,
                notificationFlag:true                
            }
        },
        methods:{
            ChangePage(val){
                this.page = val
                this.getData(this.showFlag)
                document.getElementById("top").scrollIntoView();
            },
            PrePage(){
                this.page--
            },
            NextPage(){
                this.page++
            },
            getData(flag){
                this.showFlag = flag
                axios.get('/notifications', {
                    params:{
                        filterRule:this.showFlag,
                        page:this.page
                    }
                }).then((result)=>{
                    var res = result.data
                    this.notifications = res.notifications
                    this.pagination = res.pagination
                    this.page = res.pagination.currentPage
                    this.notification_count = res.notificationCount
                })
            },
            toEditSetting(){
                this.$store.commit('ChangeEditSettingFlag', 'notification')
                this.$router.push('/main/setting')   
            },
            readAll(){
                axios.patch('/notifications/read', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if (res.type == 'success'){
                        this.notificationFlag = false
                    }
                    this.$store.commit('ClearNotificationCount')   
                })
            },
            read(notification){
                var id = notification.id
                axios.patch('/notification/' + id + '/read',{},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if (res.type == 'success'){
                        notification.isRead = true   
                    }
                    this.$store.commit('DecreaseNotificationCount')                 
                })
            }
        },
        mounted(){
            this.getData('all')
        }
    }
</script>

<style scoped>

</style>

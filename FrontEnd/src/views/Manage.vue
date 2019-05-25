<template>
    <div>
        <admin-nav></admin-nav>
        <div class="container">
            <flash-message></flash-message>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <el-breadcrumb separator-class="el-icon-arrow-right">
                    <el-breadcrumb-item>管理面板主页</el-breadcrumb-item>
                    </el-breadcrumb>
                </ol>
            </nav>
            <div class="row">
                <div class="col-md-6">
                    <div class="card border-primary mb-3">
                        <div class="card-header"><span class="oi oi-image"></span> 相片</div>
                        <div class="card-body">
                            <h4 class="card-title">总数: {{ photo_count }}</h4>
                            <p class="card-text">被举报相片数: {{ reported_photo_count }}</p>
                            <router-link class="btn btn-primary text-white" to="/admin/manage/photo">管理</router-link>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card border-secondary mb-3">
                        <div class="card-header"><span class="oi oi-people"></span> 用户</div>
                        <div class="card-body">
                            <h4 class="card-title">总数: {{ user_count }}</h4>
                            <p class="card-text">锁定数: {{ locked_user_count }}
                                封禁数: {{ blocked_user_count }}</p>
                            <router-link class="btn btn-primary text-white" to="/admin/manage/user">管理</router-link>
                            <a class="btn btn-secondary text-white" href="javascript:void(0)" @click="toUser('locked')">查看被锁定用户</a>
                            <a class="btn btn-secondary text-white" href="javascript:void(0)" @click="toUser('blocked')">查看被封禁用户</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div class="card border-danger mb-3">
                        <div class="card-header"><span class="oi oi-comment-square"></span> 评论</div>
                        <div class="card-body">
                            <h4 class="card-title">总数: {{ comment_count }}</h4>
                            <p class="card-text">被举报评论数: {{ reported_comments_count }}</p>
                            <router-link class="btn btn-primary text-white" to="/admin/manage/comment">管理</router-link>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card border-success mb-3">
                        <div class="card-header"><span class="oi oi-tag"></span> 标签</div>
                        <div class="card-body">
                            <h4 class="card-title">总数: {{ tag_count }}</h4>
                            <p class="card-text">&nbsp;</p>
                            <router-link class="btn btn-primary text-white" to="/admin/manage/tag">管理</router-link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import AdminNav from "../components/AdminNav"
    import FlashMessage from './../components/FlashMessage'
    import axios from "axios"

    export default {
        name: "Manage",
        components:{
            AdminNav,
            FlashMessage
        },
        data(){
            return{
                photo_count:0,
                reported_photo_count:0,
                user_count:0,
                locked_user_count:0,
                blocked_user_count:0,
                comment_count:0,
                reported_comments_count:0,
                tag_count:0
            }
        },
        methods:{
            getData(){
                axios.get('/management/Info').then((result)=>{
                    var res = result.data
                    this.photo_count=res.photoCount,
                    this.reported_photo_count=res.reportedPhotosCount,
                    this.user_count=res.userCount,
                    this.locked_user_count=res.lockedUserCount,
                    this.blocked_user_count=res.blockedUserCount,
                    this.comment_count=res.commentCount,
                    this.reported_comments_count=res.reportedCommentsCount,
                    this.tag_count=res.tagCount
                })
            },
            toUser(flag){
                this.$store.commit('ChangeManageFilter', flag)
                this.$router.push('/admin/manage/user')
            }
        },
        mounted(){
          this.getData()
        }
    }
</script>

<style scoped>

</style>

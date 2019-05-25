<template>
    <div class="comments" id="comments">
        <h3>{{ $store.state.Photo.photo.commentCount }} 条评论
            <small>
                <a href="javascript:void(0)" @click="lastestComment">最新评论</a>
            </small>
            <span v-if="$store.state.Photo.photo.author.username == $store.state.Nav.username">
                <form class="inline" @click="setComment($store.state.Photo.photo)">
                    <button type="button" class="btn btn-xs btn-link float-right">
                        <span v-if="$store.state.Photo.canComment">禁止</span><span v-else>允许</span>评论
                    </button>
                </form>
            </span>
        </h3>
        <hr>
        <div v-if="$store.state.Photo.comments">
            <div v-for="comment in $store.state.Photo.comments" :key="comment.id">
                <div class="comment">
                    <div class="comment-thumbnail">
                        <router-link :to="'/main/user/' + comment.author.username">
                            <el-popover placement="top-start" width="200" trigger="hover" :open-delay="500" @show="getPopupData(comment.author.id)">
                                <popup-card :user="user"></popup-card>
                                <img class="rounded img-fluid avatar-s profile-popover"
                                    :src="comment.author.avatarM" slot="reference">
                            </el-popover>
                        </router-link>
                    </div>
                    <div class="comment-body">
                        <h6>
                            <router-link class="profile-popover"
                            :to="'/main/user/' + comment.author.username">
                                {{ comment.author.name }}
                            </router-link>
                            <span v-if="comment.author.username ==  $store.state.Photo.photo.author.username">
                                <span class="badge badge-light">作者</span>
                            </span>
                            <small data-toggle="tooltip" data-placement="top"
                                data-delay="500">
                                {{ $moment(comment.timestamp).fromNow(refresh=true) }}
                            </small>
                            <div v-if="$store.state.Nav.HasLogin">
                                <span class="float-right">
                                <span class="dropdown">
                                    <button class="btn btn-sm btn-light" type="button" id="dropdownMenuButton"
                                            data-toggle="dropdown"
                                            aria-haspopup="true" aria-expanded="false">
                                    <span class="oi oi-ellipses"></span>
                                    </button>
                                    <span class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                                    <div v-if="$store.state.Nav.username != comment.author.username">
                                        <a class="dropdown-item btn" @click="replyComment(comment.id, comment.author.name)"
                                        href="javascript:void(0)">
                                        <span class="oi oi-comment-square"></span> 回复
                                    </a>
                                    </div>
                                    <div v-if="$store.state.Nav.username == comment.author.username || $store.state.Nav.username  == $store.state.Photo.photo.author.username || $store.state.Nav.CanModerate">
                                        <a class="dropdown-item" @click="deleteComment(comment.id)" href="javascript:void(0)">
                                        <span class="oi oi-trash" aria-hidden="true"></span> 删除
                                    </a>
                                    </div>
                                    <div v-if="$store.state.Nav.username != comment.author.username">
                                    <form class="inline" @click="reportComment(comment.id)">
                                        <button type="button" class="dropdown-item">
                                            <span class="oi oi-warning" aria-hidden="true"></span> 举报
                                        </button>
                                    </form>
                                    </div>
                                    </span>
                                    </span>
                                </span>
                            </div>
                        </h6>
                        <p>
                            <span v-if="comment.repliedName">
                                回复评论
                                <router-link :to="'/main/user/' + comment.repliedName">{{ comment.repliedName }}</router-link>:
                            </span>
                            {{ comment.body }}
                        </p>
                    </div>
                </div>
                <hr>
            </div>
            <div class="page-footer" v-if="$store.state.Photo.photo.commentCount">
                <pagination :pagination='$store.state.Photo.pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
            </div>
        </div>
        <div v-else>
            <p class="tip">没有评论</p>
        </div>

        <div v-if="$store.state.Photo.canComment">
            <div v-if="$store.state.Nav.HasLogin">
                <div v-if="$store.state.Nav.CanComment">
                    <div class="reply" v-if="repliedFlag">
                        <div class="alert alert-dark">
                        回复给 {{ commentRepliedAuthorName }}:
                            <a class="float-right" href="javascript:void(0)" @click="cancelReply">取消</a>
                        </div>
                    </div>
                    <div class="comment-form-area">
                        <div class="comment-form-thumbnail">
                            <img class="rounded img-fluid avatar-s"
                                :src="$store.state.Nav.AvatarPathM">
                        </div>
                        <div class="comment-form" id="comment-form" style="{padding-top: 16px}">
                            <el-input type="textarea" :rows="2" placeholder="请输入内容" v-model="textarea2"></el-input><br>
                            <el-button class="comment-form-submit" id='submit' type="success" @click="addComment">发送评论</el-button>
                        </div>
                    </div>
                </div>
                <div v-else>
                    <p class="tip">
                        没有评论权限
                    </p>
                </div>
            </div>
            <div v-else>
                <p class="tip" id="comment-form-1" @click="redirectLogin">
                    <a href="javascript:void(0)">登录</a>后进行评论
                </p>
            </div>
        </div>
        <div v-else>
            <p class="tip">
                该相片不允许评论
            </p>
        </div>
    </div>
</template>

<script> 
    import axios from 'axios'
    import PopupCard from './PopupCard'
    import pagination from './pagination'

    export default {
        name: "CommentArea",
        methods:{
            getPageData(){
                axios.get('/photo/' + this.$route.params.id, {
                    params:{
                        page: this.$store.state.Photo.page
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('pageData', {photo:res.photo, comments:res.comments, pagination:res.pagination})
                })
            },
            lastestComment(){
                this.$store.commit('changePage', this.$store.state.Photo.pagination.lastPage)
                this.getPageData()
                if (this.$store.state.Nav.HasLogin){
                    document.getElementById("comment-form").scrollIntoView()
                }else{
                    document.getElementById("comment-form-1").scrollIntoView()
                }
            },
            setComment(photo){
                axios.patch('/photo/' + photo.id +'/canComment',{},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if(res.type == 'success'){
                        this.$store.commit('changeCommentFlag', !this.$store.state.Photo.canComment)
                    }

                })
            },
            replyComment(commentId, repliedAuthorName){
                this.repliedFlag = true
                this.repliedId = commentId
                this.commentRepliedAuthorName = repliedAuthorName
                document.getElementById("comment-form").scrollIntoView()
            },
            cancelReply(){
                this.repliedFlag = false
                this.repliedId = 0
                this.commentRepliedAuthorName = ''
            },
            addComment(){
                axios.post('/photo/' + this.$store.state.Photo.photo.id + '/comment', {
                    body:this.textarea2,
                    reply:this.repliedId
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    this.repliedFlag = false
                    this.textarea2 = ''
                    this.repliedId = 0
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.lastestComment()
                })
            },
            deleteComment(id){
                this.$confirm('是否确认删除此评论', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {
                        axios.delete('/comment/' + id,{
                            headers:{
                                'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }).then((result)=>{
                            var res = result.data
                            this.$store.commit('FlashMessage', res)
                            this.getPageData()   
                            this.$message({
                                type: 'success',
                                message: '删除成功!'
                            });
                            }).catch(() => {
                            this.$message({
                                type: 'info',
                                message: '已取消删除'
                        });          
                    });
                })
            },
            reportComment(id){
                axios.patch('/comment/' + id + '/report', {},{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    } 
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                })
            },
            ChangePage(val){
                this.$store.commit('changePage', val)
                this.getPageData()
                document.getElementById("comments").scrollIntoView();
            },
            PrePage(){
                this.$store.commit('changePage', this.$store.state.Photo.page--)
            },
            NextPage(){
                this.$store.commit('changePage', this.$store.state.Photo.page++)
            },
            getPopupData(id){
            axios.get('/user/popup/'+ id).then((result)=>{
              this.user = result.data
            })
            },
            redirectLogin(){
                this.$store.commit('UpdateRedirectPage', this.$route.path)
                this.$store.commit('FlashMessage', {message:'请先登录', type:"warning"})
                this.$router.push('/')
            }      
        },
        components:{
            PopupCard,
            pagination
        },
        data(){
            return{
                repliedFlag:false,
                repliedId:0,
                commentRepliedAuthorName:'',
                textarea2:'',
                user:{}
            }
        }
    }
</script>

<style scoped>

</style>

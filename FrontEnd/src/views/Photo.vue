<template>
    <div>
        <nav-bar></nav-bar>
        <main>
            <div class="container">
                <flash-message></flash-message>
                <div class="row">
                    <div class="col-md-8">
                        <div class="photo">
                            <a :href="$store.state.Photo.photo.filename" target="_blank">
                                <img class="img-fluid" :src="$store.state.Photo.photo.filenameM">
                            </a>
                        </div>
                        <a class="btn btn-primary btn-sm text-white" data-toggle="modal" data-target="#share-modal" href="javascript:void(0)">分享</a>
                        <span v-if="($store.state.Nav.username == $store.state.Photo.photo.author.username) || $store.state.Nav.CanModerate">
                            <el-popover placement="top" width="160" v-model="visible2">
                                <p>确定要删除此张相片吗？</p>
                                <div style="text-align: right; margin: 0">
                                    <el-button size="mini" type="text" @click="visible2 = false">取消</el-button>
                                    <el-button type="primary" size="mini" @click="deletePhoto($store.state.Photo.photo.id)">确定</el-button>
                                </div>
                                <el-button slot="reference" class="btn btn-danger btn-sm" size="small">删除</el-button>
                            </el-popover>
                        </span>
                        <span v-if="$store.state.Nav.HasLogin && ( $store.state.Nav.username != $store.state.Photo.photo.author.username )">
                            <form class="inline">
                                <button type="button" class="btn btn-link btn-sm" @click="reportPhoto($store.state.Photo.photo.id)">举报</button>
                            </form>
                        </span>
                        <p class="text-muted float-right small">
                            <span class="oi oi-clock"></span> 上传时间 {{ $moment($store.state.Photo.photo.timestamp).format('LL') }}
                        </p>
                        <comment-area></comment-area>
                    </div>
                    <div class="col-md-4">
                        <photo-side-bar></photo-side-bar>
                    </div>
                </div>
                <!-- 分享模态框 -->
                <div class="modal fade" id="share-modal" tabindex="-1" role="dialog" aria-labelledby="shareModalLabel">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="shareModalLabel">分享链接</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body w-100">
                                <input class="form-control" :value="url"
                                    readonly>
                            </div>
                        </div>
                    </div>
                </div>             
            </div>
        </main>
    </div>
</template>

<script> 
    import axios from 'axios'
    import NavBar from './../components/NavBar'
    import FooterUltimate from './../components/FooterUltimate'
    import FlashMessage from './../components/FlashMessage'
    import PhotoSideBar from './../components/PhotoSideBar'
    import CommentArea from './../components/CommentArea'

    export default {
        name: "Photo",
        data(){
            return{
                visible2: false,
                reLoadPhotoId:0,
                url:window.location.href
            }
        },
        mounted(){
            this.getData()
        },
        methods:{
            getData(){
                axios.get('/photo/' +this.$route.params.id).then((result)=>{
                    var res = result.data
                    this.$store.commit('pageData', {photo:res.photo, comments:res.comments, pagination:res.pagination})
                })
            },
            deletePhoto(id){
                var id = id
                axios.delete('/photo/' + id,
                {
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    if (res.message){
                        this.$store.commit('FlashMessage', {message:res.message, type:res.type})
                    }                    
                    this.reLoadPhotoId = res.photoId
                    if (this.reLoadPhotoId == 0){
                        var redirectUrl = '/main/user/' + this.$store.state.Photo.photo.author.username
                        this.$router.push(redirectUrl)
                    }else{
                        axios.get('/photo/' + this.reLoadPhotoId).then((result)=>{
                            this.visible2 = false
                            var res = result.data
                            this.$store.commit('pageData', {photo:res.photo, comments:res.comments, pagination:res.pagination})
                            this.$router.push('/main/photo/' + res.photo.id)
                        })                        
                    }
                })
            },
            reportPhoto(id){
                var id = id
                axios.patch('/photo/' + id +'/report', {},
                {
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                }                 
            )},
            
        },
        components:{
            NavBar,
            FooterUltimate,
            FlashMessage,
            PhotoSideBar,
            CommentArea
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

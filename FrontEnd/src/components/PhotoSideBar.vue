<template>
    <div>
        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li class="page-item">
                    <a class="page-link" href="javascript:void(0)" @click="pre($store.state.Photo.photo.id)">&larr;上一个</a>
                </li>
                <li class="page-item">
                    <a class="page-link" href="javascript:void(0)" @click="next($store.state.Photo.photo.id)">下一个&rarr;</a>
                </li>
            </ul>
        </nav>
        <div class="card bg-light mb-3 w-100 sidebar-card">
            <div class="card-body">
                <div class="row">
                    <router-link :to="'/main/user/' + $store.state.Photo.photo.author.username">
                        <img class="sidebar-avatar rounded avatar-m"
                            :src="$store.state.Photo.photo.author.avatarM">
                    </router-link>
                    <div class="sidebar-profile">
                        <h6 class="card-title">
                            <router-link :to="'/main/user/' + $store.state.Photo.photo.author.username">{{ $store.state.Photo.photo.author.name }}</router-link>
                        </h6>
                        <p class="card-subtitle mb-2 text-muted">{{ $store.state.Photo.photo.author.username }}</p>
                        <follow-area :user='$store.state.Photo.photo.author' ></follow-area>
                    </div>
                </div>
            </div>
        </div>
        <div class="card bg-light mb-3 w-100">
            <div class="card-body">
                <div id="description">
                    <p>
                        <span v-if="$store.state.Photo.description">
                            {{ $store.state.Photo.description }}
                        </span>
                        <span >
                            <a v-if="$store.state.Photo.photo.author.username == $store.state.Nav.username  && !EditDescriptionFlag" id="description-btn" href="javascript:void(0)" @click="EditDescriptionFlag = true">
                                <small><span class="oi oi-pencil"></span> 编辑图片简介</small>
                            </a>
                        </span>
                    </p>              
                    <div v-if="$store.state.Photo.photo.author.username == $store.state.Nav.username  && EditDescriptionFlag">
                        <div id="description-form">
                            <form>
                                <label class="form-control-label" for="photoDescription">图片描述</label><br>
                                <el-input  id="photoDescription" class="input" type="textarea" :rows="2" v-model="textarea"></el-input>
                                <el-row>
                                    <el-button @click="EditDescriptionFlag = false">取消</el-button>
                                    <el-button type="primary" @click="EditDescription($store.state.Photo.photo.id)">提交</el-button>
                                </el-row>
                            </form>
                        </div>
                    </div>
                </div>
                <div id="tags">
                    <p>
                        <span v-if="$store.state.Photo.tags">
                            <span v-for="tag in $store.state.Photo.tags" :key="tag.id">
                                <router-link class="badge badge-light"
                                :to="'/main/tag/' + tag.id"><span
                                        class="oi oi-tag"></span> {{ tag.name }}</router-link>
                            </span>
                        </span>
                        <span v-if="$store.state.Photo.photo.author.username == $store.state.Nav.username  && !EditTagFlag">
                            <a id="tag-btn" href="javascript:void(0)"  @click="EditTagFlag = true">
                                <small><span class="oi oi-pencil"></span> 编辑标签</small>
                            </a>
                        </span>
                    </p>
                </div>
                <div v-if="$store.state.Photo.photo.author.username == $store.state.Nav.username  && EditTagFlag">
                    <div id="tag-form">
                        <form>
                            <label class="form-control-label" for="tag">添加标签(添加多个标签使用空格分隔)</label><br>
                            <el-input id="tag" class="input" v-model="addTag" clearable></el-input>
                            <el-row>
                                <el-button @click="EditTagFlag = false">取消</el-button>
                                <el-button type="primary" @click="AddTags($store.state.Photo.photo.id)">提交</el-button>
                            </el-row>
                        </form>
                        <div v-if="$store.state.Photo.tags">
                            <hr>
                            <div v-for="tag in $store.state.Photo.tags" :key="tag.id">
                                <a href="javascript:void(0)">
                                <span class="badge badge-danger" @click="deleteTag($store.state.Photo.photo.id, tag.id)">
                                    {{ tag.name }} <span class="oi oi-trash" aria-hidden="true"></span>
                                </span>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-if="$store.state.Nav.HasLogin">
                    <div v-if="$store.state.Photo.isCollected">
                    <el-button  size="medium" class="btn btn-outline-secondary btn-sm uncollect-btn"
                            @click="uncollectPhoto($store.state.Photo.photo.id)">
                        <span class="oi oi-x"></span> 取消收藏
                    </el-button>
                    </div>
                    <div v-else>
                    <el-button  size="medium" class="btn btn-outline-primary btn-sm collect-btn"
                            @click="collectPhoto($store.state.Photo.photo.id)">
                        <span class="oi oi-star"></span> 收藏
                    </el-button>
                    </div>
                </div>
                <div v-else>
                    <form class="inline" @click="redirectLogin">
                        <a href="javascript:void(0)">
                            <el-button  type="button" size="medium" class="btn btn-primary btn-sm">
                                <span class="oi oi-star"></span> 收藏
                            </el-button>
                        </a>
                    </form>
                </div>
                <div v-if="$store.state.Photo.photo.collectorCount">
                    <router-link :to="'/main/collectors/' + $store.state.Photo.photo.id">{{ $store.state.Photo.photo.collectorCount }}
                        个收藏者</router-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from 'axios'
    import FollowArea from './FollowArea'

    export default {
        name: "PhotoSideBar",
      components: {FollowArea},
      comments:{
          FollowArea
        },
        data(){
            return{
                EditDescriptionFlag:false,
                EditTagFlag:false,
                textarea: this.$store.state.Photo.description,
                addTag:"",
                visible:false
            }
        },
        methods:{
            pre(id){
                axios.get('/photo/' + id + '/pre').then((result)=>{
                    var res = result.data
                    if(res.message){
                        this.$store.commit('FlashMessage', res)
                    }else{
                        axios.get('/photo/' + res.photoId).then((result)=>{
                            var res = result.data
                            this.$store.commit('pageData', {photo:res.photo, comments:res.comments, pagination:res.pagination})
                            this.$router.push('/main/photo/' + res.photo.id)
                        })                        
                    }
                })},
            next(id){
                var id = id
                axios.get('/photo/' + id + '/next').then((result)=>{
                    var res = result.data
                    if(res.message){
                        this.$store.commit('FlashMessage', res)
                    }else{
                        axios.get('/photo/' + res.photoId).then((result)=>{
                            var res = result.data
                            this.$store.commit('pageData', {photo:res.photo, comments:res.comments, pagination:res.pagination})
                            this.$router.push('/main/photo/' + res.photo.id)
                        })                        
                    }
                })},
            EditDescription(id){
                this.EditDescriptionFlag = false
                axios.patch('/photo/' + id + '/description', {
                    description:this.textarea                    
                    },{
                        headers:{
                            'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }
                    ).then((result)=>{
                        var res = result.data
                        this.$store.commit('FlashMessage', {message:res.message, type:res.type})
                        this.$store.commit('changeDescription', res.description)
                    })
            },
            AddTags(id){
                this.EditTagFlag = false
                axios.post('/photo/' + id + '/tag', {
                    tags:this.addTag                   
                    },{
                        headers:{
                            'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }
                    ).then((result)=>{
                        var res = result.data
                        this.$store.commit('FlashMessage', {message:res.message, type:res.type})
                        this.$store.commit('changeTags', res.tags)
                    })
            },
            uncollectPhoto(id){
                axios.delete('/collection/' + id,{
                    headers:{
                       'X-CSRFToken': this.$store.state.Page.CSRFToken
                }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if(res.type == 'success'){
                        this.$store.commit('changeCollectFlag', !this.$store.state.Photo.isCollected)
                    }
                })
            },
            collectPhoto(id){
                axios({
                    method: 'post',
                    url: '/collection/' + id,
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                        }
                    }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if(res.type == 'success'){
                        this.$store.commit('changeCollectFlag', !this.$store.state.Photo.isCollected)
                    }
                })
            },
            redirectLogin(){
                this.$store.commit('UpdateRedirectPage', this.$route.path)
                this.$store.commit('FlashMessage', {message:'请先登录', type:"warning"})
                this.$router.push('/')
            },
            deleteTag(photo_id, tag_id){
                this.$confirm('是否确认删除此评论', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {                      
                        axios.delete('/photo/' + photo_id + '/tag/' + tag_id ,{
                            headers:{
                                'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }).then((result)=>{
                            var res = result.data
                            this.EditTagFlag = false
                            this.$store.commit('changeTags', res.tags) 
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
        }
        
    }
</script>

<style scoped>
.input {
    margin-bottom: .5rem
}
</style>

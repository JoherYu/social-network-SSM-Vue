<template>
  <div>
    <admin-nav></admin-nav>
    <div class="container" id="top">
        <flash-message></flash-message>
      <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
              <el-breadcrumb separator-class="el-icon-arrow-right">
              <el-breadcrumb-item :to="{ path: '/admin/manage' }">管理面板主页</el-breadcrumb-item>
              <el-breadcrumb-item>管理相片</el-breadcrumb-item>
              </el-breadcrumb>
          </ol>
      </nav>
      <div class="page-header">
          <h1>相片数
              <small class="text-muted">{{ pagination.count }}</small>
              <span class="dropdown">
              <button class="btn btn-secondary btn-sm" type="button" id="dropdownMenuButton" data-toggle="dropdown"
                      aria-haspopup="true" aria-expanded="false">
                  根据 {{ orderRule }} 排序 <span class="oi oi-elevator"></span>
              </button>
              <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                  <div v-if="order == 'byFlag'">
                      <a class="dropdown-item" href="javascript:void(0)" @click="getData('byTime')">根据时间排序</a>
                  </div>
                  <div v-else>
                      <a class="dropdown-item" href="javascript:void(0)" @click="getData('byFlag')">根据举报数排序</a>
                  </div>
              </div>
          </span>
          </h1>
      </div>
      <div v-if="photoCount">
          <table class="table table-striped">
              <thead>
              <tr>
                  <th>图像</th>
                  <th>图像描述</th>
                  <th>标签</th>
                  <th>作者</th>
                  <th>举报数</th>
                  <th>上传日期</th>
                  <th>处理</th>
              </tr>
              </thead>
                <tr v-for="photo in photos" :key="photo.id">
                    <td>
                        <router-link :to="'/main/photo/' + photo.id">
                            <img :src="photo.filenameS" width="250">
                        </router-link>
                    </td>
                    <td>{{ photo.description }}</td>
                    <div v-if="photo.tagCount">
                      <td v-for="tag in photo.tags" :key="tag.id">
                        <a href="javascript:void(0)">
                        <span class="badge badge-danger" @click="deleteTag(photo.id, tag.id)">
                            {{ tag.name }} <span class="oi oi-trash" aria-hidden="true"></span>
                        </span>
                        </a>
                      </td>
                    </div>
                    <td>
                        <router-link :to="'/main/user/' + photo.authorUsername">{{ photo.authorName }}</router-link>
                    </td>
                    <td>{{ photo.flag }}</td>
                    <td>{{ $moment(photo.timestamp).format('LL') }}</td>
                    <td>
                      <el-button type="primary" size="mini" @click="deletePhoto(photo.id)">删除</el-button>
                    </td>
                </tr>
          </table>
          <div class="page-footer"><pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination></div>
      </div>
      <div v-else>
          <div class="tip"><h5>没有图像</h5></div>
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
        name: "ManagePhoto",
        components:{
          AdminNav,
          pagination,
          FlashMessage
        },
        data(){
          return{
            orderRule:'时间',
            order:'byTime',
            photos:[],
            pagination:{},
            page:1,
            photoCount:0           
          }
        },
        mounted(){
          this.getData(this.order)
        },
        methods:{
              getData(order){
                this.order = order
                axios.get('/management/photos',{
                    params:{
                        orderRule:this.order,
                        page:this.page
                    }
                }).then((result)=>{
                    var res =result.data
                    this.photos = res.photos
                    this.pagination = res.pagination
                    this.photoCount = res.photoCount
                    if (this.order == 'byTime'){
                        this.orderRule = '时间'
                        
                    }else(
                        this.orderRule = '被举报数'
                    )
                })
            },
            deleteTag(photo_id, tag_id){
                var photo_id = photo_id
                var tag_id = tag_id
                this.$confirm('是否确认删除此标签', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {                      
                        axios.delete('photo/' + photo_Id + '/tag/' + tag_id, {
                            headers:{
                                'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }).then((result)=>{
                            var res = result.data
                            this.getData(this.order)
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
            deletePhoto(id){
                this.$confirm('是否确认删除此图片', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {                      
                        axios.delete('/photo/' + id, {
                            headers:{
                                'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }).then((result)=>{
                            var res = result.data
                            this.getData(this.order)
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
            ChangePage(val){
                this.page = val
                this.getData(this.order)
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

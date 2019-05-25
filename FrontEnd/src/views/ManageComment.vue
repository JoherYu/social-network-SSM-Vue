<template>
  <div>
    <admin-nav></admin-nav>
    <div class="container" id="top">
      <flash-message></flash-message>
      <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
              <el-breadcrumb separator-class="el-icon-arrow-right">
              <el-breadcrumb-item :to="{ path: '/admin/manage' }">管理面板主页</el-breadcrumb-item>
              <el-breadcrumb-item>管理评论</el-breadcrumb-item>
              </el-breadcrumb>
          </ol>
      </nav>
      <div class="page-header">
          <h1>评论数
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
      <div v-if="commentCount">
          <table class="table table-striped">
              <thead>
              <tr>
                  <th>内容</th>
                  <th>评论者</th>
                  <th>图像序号</th>
                  <th>举报数</th>
                  <th>发布日期</th>
                  <th>处理</th>
              </tr>
              </thead>
                <tr v-for="comment in comments" :key="comment.id">
                    <td>{{ comment.body }}</td>
                    <td>
                        <router-link :to="'/main/user/' + comment.authorUsername">{{ comment.authorName }}</router-link>
                    </td>
                    <td>
                        <router-link :to="'/main/photo/' + comment.photoId">Photo {{ comment.photoId }}</router-link>
                    </td>
                    <td>{{ comment.flag }}</td>
                    <td>{{ $moment(comment.timestamp).format('LL') }}</td>
                    <td>
                      <el-button type="primary" size="mini" @click="deleteComment(comment.id)">删除</el-button>
                    </td>
                </tr>
          </table>
          <div class="page-footer"><pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination></div>
      </div>
      <div v-else>
          <div class="tip"><h5>没有评论</h5></div>
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
        name: "ManageComment",
        components:{
          AdminNav,
          pagination,
          FlashMessage
        },
        mounted(){
          this.getData(this.order)
        },
        data(){
          return{
            orderRule:'时间',
            order:'byTime',
            comments:[],
            pagination:{},
            page:1,
            commentCount:0
          }
        },
        methods:{
            getData(order){
                this.order = order
                axios.get('/management/comments',{
                    params:{
                        orderRule:this.order,
                        page:this.page
                    }
                }).then((result)=>{
                    var res =result.data
                    this.comments = res.comments
                    this.pagination = res.pagination
                    this.commentCount = res.commentCount
                    if (this.order == 'byTime'){
                        this.orderRule = '时间'
                        
                    }else(
                        this.orderRule = '被举报数'
                    )
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
                            getData(this.order)
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

<template>
  <div>
    <admin-nav></admin-nav>
    <div class="container" id="top">
        <flash-message></flash-message>
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/admin/manage' }">管理面板主页</el-breadcrumb-item>
                <el-breadcrumb-item>管理标签</el-breadcrumb-item>
                </el-breadcrumb>
            </ol>
        </nav>
        <div class="page-header">
            <h1>标签数
                <small class="text-muted">{{ pagination.count }}</small>
            </h1>
        </div>
        <div v-if="tag_count">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>标签名</th>
                    <th>相片数</th>
                    <th>处理</th>
                </tr>
                </thead>
                    <tr v-for="tag in tags" :key="tag.id">
                        <td>{{ tag.id }}</td>
                        <td>{{ tag.name }}</td>
                        <td><router-link :to="'/main/tag/' + tag.id">{{ tag.photoCount }}</router-link></td>
                        <td>
                            <a @click="deleteTag(tag.id)" href="javascript:void(0)">
                                        <span class="oi oi-trash" aria-hidden="true"></span> 删除
                            </a>
                        </td>
                    </tr>
            </table>
            <div class="page-footer"><pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
            </div>
        </div>
        <div v-else>
            <div class="tip"><h5>没有标签</h5></div>
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
        name: "ManageTag",
        components:{
          AdminNav,
          pagination,
          FlashMessage
        },
        data(){
            return{
                pagination:{},
                page:1,
                tags:[],
                tag_count:0,              
            }
        },
        mounted(){
            this.getData()
        },
        methods:{
            ChangePage(val){
                this.page = val
                this.getData()
                document.getElementById("top").scrollIntoView();
            },
            PrePage(){
                this.page--
            },
            NextPage(){
                this.page++
            },  
            deleteTag(id){
                var id = id
                this.$confirm('是否确认删除此标签', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {                      
                        axios.delete('/tag/' + id, {
                            headers:{
                                'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }).then((result)=>{
                            var res = result.data
                            this.getData()
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
            getData(){
                axios.get('/management/tags').then((result)=>{
                    var res = result.data
                    this. pagination = res.pagination
                    this.tags = res.tags
                    this.tag_count = res.tagCount
                })
            }     
        }
    }
</script>

<style scoped>

</style>

<template>
    <div>
        <nav-bar></nav-bar>
        <main>
            <div class="container" id="top">
                <flash-message></flash-message>
                <div class="page-header">
                    <h1>#{{ tag.name }}
                        <small class="text-muted">{{ photoCount }} 张相片</small>
                        <span v-if="$store.state.Nav.CanModerate">
                            <el-popover placement="top" width="160" v-model="visible">
                                <p>确定要删除此标签吗？</p>
                                <div style="text-align: right; margin: 0">
                                    <el-button size="mini" type="text" @click="visible = false">取消</el-button>
                                    <el-button type="primary" size="mini" @click="deleteTag">确定</el-button>
                                </div>
                                <el-button slot="reference" class="btn btn-danger btn-sm" size="small">删除</el-button>
                            </el-popover>
                        </span>
                        <span class="dropdown">
                        <button class="btn btn-secondary btn-sm" type="button" id="dropdownMenuButton" data-toggle="dropdown"
                                aria-haspopup="true" aria-expanded="false">
                            根据{{ orderRule }}排序 <span class="oi oi-elevator"></span>
                        </button>
                        <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                            <div v-if="order == 'byCollects'">
                                <a class="dropdown-item" href="javascript:void(0)" @click="getData('byTime')">根据时间排序</a>
                            </div>
                            <div v-else>
                                <a class="dropdown-item" href="javascript:void(0)" @click="getData('byCollects')">根据收藏数排序</a>
                            </div>
                        </div>
                    </span>
                    </h1>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div v-for="photo in photos" :key="photo.id" >
                            <photo-card :photo="photo"></photo-card>
                        </div>
                    </div>
                    <div class="page-footer">
                        <br><pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
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
    import PhotoCard from "../components/PhotoCard";
    import pagination from "../components/pagination";    
    import axios from 'axios'

    export default {
        name: "tag",
        components: {FooterUltimate, FlashMessage, NavBar, pagination, PhotoCard},
        data(){
            return{
                orderRule:'时间',
                order:'byTime',
                photos:[],
                pagination:{},
                tag:{},
                page:1,
                visible:false,
                photoCount:0
            }
        },
        methods:{
            getData(order){
                this.order = order
                axios.get('/tag/' + this.$route.params.id + '/photos',{
                    params:{
                        filterRule:this.order,
                        page:this.page
                    }
                }).then((result)=>{
                    var res =result.data
                    this.photos = res.photos
                    this.tag = res.tag
                    this.pagination = res.pagination
                    this.photoCount = res.photoCount
                    if (this.order == 'byTime'){
                        this.orderRule = '时间'         
                    }else(
                        this.orderRule = '收藏数'
                    )
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
            deleteTag(){
                this.$confirm('是否确认删除此标签', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {                      
                        axios.delete('/tag/' + this.$route.params.id ,{
                            headers:{
                                'X-CSRFToken': this.$store.state.Page.CSRFToken
                            }
                        }).then((result)=>{
                            this.$router.go(-1)
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
        },
        mounted(){
            this.getData(this.order)
        }
    }
</script>

<style scoped>

</style>

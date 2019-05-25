<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <div class="container" id="top">
        <flash-message></flash-message>
        <div class="page-header">
            <div class="row">
                <div class="col-md-12">
                    <a class="btn btn-default btn-sm" href="javascript:void(0)" @click="getBack">
                        <span class="oi oi-arrow-left" aria-hidden="true"></span> 返回
                    </a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <h3>{{ collector_count }} 个收藏者</h3>
                <div v-for="collect in collects" :key="collect.id">
                    <user-card :user='collect'></user-card>
                </div>
            </div>
        </div>
        <div v-if="collector_count">
            <div class="page-footer">
                <pagination :pagination='pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
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
    import axios from 'axios';
    import UserCard from "../components/UserCard";

    export default {
        name: "Collector",
        data(){
            return{
                collector_count:0,
                collects:[],
                pagination:{},
                page:1
            }
        },
        methods:{
            getBack(){
                this.$router.go(-1)
            },
            getData(){
                axios.get('/photo/' + this.$route.params.id + '/collectors').then((result)=>{
                    var res = result.data
                    this.collector_count = res.collectorCount
                    this.collects = res.collects
                    this.pagination = res.pagination
                })
            },
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
        },
        components:{
            FooterUltimate, 
            FlashMessage, 
            NavBar, 
            pagination,
            UserCard
        },
        mounted(){
            this.getData()
        }
    }
</script>

<style scoped>

</style>

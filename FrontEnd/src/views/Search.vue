<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <div class="container">
        <flash-message></flash-message>
        <div class="page-header" id="top">
          <h1>搜索: {{ $store.state.Nav.query }}</h1>
        </div>
        <div class="row">
          <div class="col-md-3">
            <div class="nav nav-pills flex-column" role="tablist" aria-orientation="vertical">
              <router-link class="nav-item nav-link" :class="{ active: $store.state.Page.SearchCategory == 'photo' }"
                 to="/main/search" @click.native="SearchParam('photo')">相片</router-link>
              <router-link class="nav-item nav-link" :class="{ active: $store.state.Page.SearchCategory == 'user' }"
                 to="/main/search" @click.native="SearchParam('user')">用户</router-link>
              <router-link class="nav-item nav-link" :class="{ active: $store.state.Page.SearchCategory == 'tag' }"
                 to="/main/search" @click.native="SearchParam('tag')">标签</router-link>
            </div>
          </div>
          <div class="col-md-9" v-loading="this.$store.state.Nav.SearchLoading" element-loading-text="拼命加载中">
            <div v-if="$store.state.Nav.SearchItemsCount">
                <h5>{{ $store.state.Nav.SearchItemsCount }} 条相符结果</h5>
                <div v-for="item in $store.state.Nav.SearchItems" :key="item.id" >
                    <div v-if=" $store.state.Page.SearchCategory == 'photo' ">
                    <photo-card :photo="item"></photo-card>
                    </div>
                    <div v-else-if=" $store.state.Page.SearchCategory == 'user' ">
                    <user-card :user='item'></user-card>
                    </div>
                    <div v-else>
                    <tag-card :tag='item'></tag-card>
                    </div>
                </div>
            </div>
            <div v-else>
                <h5 class="tip">没有找到符合条件的结果</h5>
            </div>
          </div>
          <div v-if="$store.state.Nav.SearchItemsCount" class="page-footer">
            <search-pagination @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></search-pagination>
          </div>
        </div>
        <footer-ultimate></footer-ultimate>
      </div>
    </main>
  </div>
</template>

<script>
    import NavBar from './../components/NavBar'
    import FooterUltimate from './../components/FooterUltimate'
    import FlashMessage from './../components/FlashMessage'
    import PhotoCard from './../components/PhotoCard'
    import UserCard from './../components/UserCard'
    import TagCard from './../components/TagCard'
    import axios from 'axios'
    import SearchPagination from './../components/SearchPagination'

    export default {
        name: "Search",
        data(){
            return{
              page:1
            }
        },
        components:{
            FlashMessage,
            NavBar,
            FooterUltimate,
            PhotoCard,
            UserCard,
            SearchPagination,
            TagCard
        },
        mounted(){
          this.Search()
        },
        methods:{
            SearchParam(index){
                this.$store.commit('SearchCategory', index)
                this.$store.commit('ResetPage', 1)
                this.Search()
            },               
            Search(){
                axios.get('/search' + '/' + this.$store.state.Page.SearchCategory + '/' + this.$store.state.Nav.query, {
                params:{
                    page:this.$store.state.SearchPagination.CurrentPage
                }
                }).then((result)=>{
                var res = result.data
                if (res.message) {
                    this.$store.commit('MessageFlag')
                    this.$store.commit('FlashMessage', {
                    message: res.message,
                    type: res.type
                    })
                } else {
                    this.$store.commit('SendSearchData', res)
                }
            })
            },
          ChangePage(val){
            this.$store.commit('ResetPage', val)
            this.Search()
            document.getElementById("top").scrollIntoView();
          },
          PrePage(){
            this.$store.commit('DecreasePage')
          },
          NextPage(){
            this.$store.commit('IncreasePage')
          }
        }
    }
</script>

<style scoped>

</style>

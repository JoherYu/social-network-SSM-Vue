<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <div class="container" id="top">
        <flash-message></flash-message>
        <div class="row justify-content-md-center">
          <div class="col-md-8">
            <div v-if="photoCount">
            <div v-for="photo in this.photos" :key="photo.id">
            <div class="card mb-3 w-100 bg-light">
              <div class="card-header">
                <router-link class="dead-link" :to="'/main/user/' + photo.author.username">
                  <el-popover placement="top-start" width="200" trigger="hover" :open-delay="500" @show="getPopupData(photo.author.id)">
                    <popup-card :user="user"></popup-card>
                    <img class="rounded img-fluid avatar-s profile-popover"
                        :src="photo.author.avatarM" slot="reference">
                  </el-popover>
                </router-link>
                <router-link class="profile-popover trend-card-avatar"
                   :to="'/main/user/' + photo.author.username">{{ photo.author.name }}</router-link>
                <span class="float-right">
                <small data-toggle="tooltip" data-placement="top" :data-timestamp="photo.timestamp"
                       data-delay="500">
                    {{ $moment(photo.timestamp).fromNow(refresh=true) }}</small></span>
              </div>
              <div class="card-body">
                <div class="" align="center">
                  <router-link class="thumbnail" :to="'/main/photo/' + photo.id">
                    <img class="img-fluid"
                         :src="photo.filenameM">
                  </router-link>
                </div>
              </div>
              <div class="card-footer">
                <span class="oi oi-star"></span>
                <span :id="'collectors-count-' + photo.id">
                                {{ photo.collectorCount }}
                            </span>
                <span class="oi oi-comment-square"></span> {{ photo.commentCount }}
                <div class="float-right">
                  <div v-if="$store.state.Nav.HasLogin">
                  <el-button :loading='loading' :class="{ hide: !photo.collected}" class="btn btn-outline-secondary btn-sm uncollect-btn"
                          @click="uncollectPhoto(photo)">
                    <span class="oi oi-x"></span> 取消收藏
                  </el-button>
                  <el-button :loading='loading' :class="{ hide: photo.collected }" class="btn btn-outline-primary btn-sm collect-btn"
                          @click="collectPhoto(photo)">
                    <span class="oi oi-star"></span> 收藏
                  </el-button>
                  </div>
                </div>
                <p v-if="photo.description" class="card-text">{{ photo.description }}</p>
              </div>
            </div>
            </div>
            </div>
            <div v-else>
            <div class="tip text-center">
              <h3>没有动态</h3>
              <p><router-link to="/main/explore">发现</router-link></p>
            </div>
            </div>
          </div>
          <div class="col-md-3">
            <home-side-bar :tags='this.tags'></home-side-bar>
          </div>
          <div v-if="this.pagination.count" class="page-footer">
            <pagination :pagination='this.pagination'  @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'></pagination>
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
    import axios from 'axios'
    import HomeSideBar from './../components/HomeSideBar'
    import pagination from './../components/pagination'
    import FlashMessage from './../components/FlashMessage'
    import PopupCard from './../components/PopupCard'

    export default {
        name: "Home",
        components:{
          NavBar,
          FooterUltimate,
          HomeSideBar,
          pagination,
          FlashMessage,
          PopupCard
        },
        data(){
          return{
            page:1,
            photos:[],
            tags:{},
            pagination:{},
            message:{},
            loading:false,
            user:{},
            photoCount:0
          }
        },
        mounted(){
          this.getData()
        },
        methods:{
          getData(){
            axios.get('/user/CenterData', {params: {page: this.page}}).then(
              (result)=>{
              var res = result.data
              this.photos = res.photos
              this.tags = res.tags
              this.pagination = res.pagination
              this.page = pagination.currentPage
              this.photoCount = res.photoCount
            })
          },
          uncollectPhoto(photo){
            this.loading = !this.loading
            axios.delete('/collection/' + photo.id, {
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
              }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              if(res.type == 'success'){
                this.loading = !this.loading
                photo.collected = !photo.collected
              }

            })
          },
          collectPhoto(photo){
            this.loading = !this.loading
            axios({
              method: 'post',
              url: '/collection/' + photo.id,
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
                }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              if(res.type == 'success'){
                this.loading = !this.loading
                photo.collected = !photo.collected
              }
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
          getPopupData(id){
            axios.get('/user/popup/' + id).then((result)=>{
              this.user = result.data
            })
          }
        }
    }
</script>

<style scoped>

</style>

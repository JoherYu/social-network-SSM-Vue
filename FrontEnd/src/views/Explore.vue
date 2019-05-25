<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <div class="container">
        <flash-message></flash-message>
          <div class="row">
            <div class="col-md-12">
              <div v-for="photo in photos" :key="photo.id">
                <photo-card :photo="photo"></photo-card>
              </div>
            </div>
          </div>
        <div class="text-center modify2">
          <a class="btn btn-primary" href="javascript:void(0)" @click="getData">
            <span class="oi oi-loop-circular"></span> 刷新
          </a>
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
    import axios from 'axios'

    export default {
        name: "Explore",
        components: {PhotoCard, FooterUltimate, FlashMessage, NavBar},
        data(){
          return{
            photos:[]
          }
        },
        methods:{
          getData(){
            axios.get('/photos/random').then((result)=>{
              var res = result.data
              this.photos = res
            })
          }
        },
        mounted(){
          this.getData()
        }
    }
</script>

<style scoped>
.modify2{margin-top:18px} 
</style>

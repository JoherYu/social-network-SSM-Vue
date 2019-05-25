<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import './../node_modules/bootstrap/dist/css/bootstrap.min.css'
import './../node_modules/bootstrap/dist/js/bootstrap.min.js'

export default {
  name: 'App',
  created(){
    if (localStorage.getItem("username")){
      //在页面加载时读取localStorage里的状态信息
      localStorage.getItem("userMsg") && this.$store.replaceState(Object.assign(this.$store.state,JSON.parse(localStorage.getItem("userMsg"))));
    
      //在页面刷新时将vuex里的信息保存到localStorage里
      window.addEventListener("beforeunload",()=>{
        localStorage.setItem("userMsg",JSON.stringify(this.$store.state))
      })
    }else{
      sessionStorage.getItem("userMsg") && this.$store.replaceState(Object.assign(this.$store.state,JSON.parse(sessionStorage.getItem("userMsg"))));
      
      window.addEventListener("beforeunload",()=>{
        sessionStorage.setItem("userMsg",JSON.stringify(this.$store.state))
      })    
    }
  }
}
</script>

<style>

@import 'assets/open-iconic/font/css/open-iconic-bootstrap.css';
@import 'assets/css/style.css'

</style>

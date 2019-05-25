<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <flash-message></flash-message>
      <div class="container">
        <div class="page-header">
            <h1>重置密码</h1>
        </div>
        <div class="row">
            <div class="col-sm-4">
                <div class="card mb-3 w-100 bg-light">
                    <div class="card-body">
                      <el-form label-position="top" label-width="80px" :model="emailData" :rules="rulesEmail" >
                          <label class="form-control-label">邮箱地址</label>
                          <el-form-item prop="email">
                          <el-input v-model="emailData.email"></el-input>
                          </el-form-item>
                          <el-button class="submit" type="primary" @click="sendResetEmail">发送</el-button>
                      </el-form>
                    </div>
                </div>
            </div>
        </div>
        <footer-ultimate></footer-ultimate>
      </div>
    </main>
  </div>
</template>

<script>
    import axios from 'axios';
    import NavBar from "../components/NavBar";
    import FlashMessage from "../components/FlashMessage";
    import FooterUltimate from "../components/FooterUltimate";

    export default {
        name: "ForgetPassword",
        components: {FooterUltimate, FlashMessage, NavBar},
        methods:{
            sendResetEmail(){
                axios.patch('/password',{
                  email: this.emailData.email
                },{
                  headers:{
                    'X-CSRFToken': this.$store.state.Page.CSRFToken
                  }
                }).then((result)=>{
                  var res = result.data
                  this.$store.commit('FlashMessage', res)
                  if (res.type == 'success'){
                    this.$router.push('/')
                  } 
                })
            }
        },
        data(){
          return{
            emailData: {
                  email:''
              },
              rulesEmail: {
                email:　[
                    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                ]
            },
          }
        }
    }
</script>

<style scoped>

</style>

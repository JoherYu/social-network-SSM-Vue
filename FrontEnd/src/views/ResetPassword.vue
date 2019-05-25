<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <flash-message></flash-message>
      <div class="container">
        <div class="row">
          <div class="col-sm-4">
            <div class="card mb-3 w-100 bg-light">
              <div class="card-body">
                <el-form label-position="top" label-width="80px" :model="passwordData" :rules="rulesPassword" >
                    <label class="form-control-label">邮箱地址</label>
                    <el-form-item prop="email">
                    <el-input v-model="passwordData.email"></el-input>
                    </el-form-item>
                    <label class="form-control-label">新密码</label>
                    <el-form-item prop="newPassword">                                    
                    <el-input type="password" v-model="passwordData.newPassword"></el-input>
                    </el-form-item>
                    <label class="form-control-label">确认密码</label>
                    <el-form-item prop="confirmPassword">                                      
                    <el-input type="password" v-model="passwordData.confirmPassword"></el-input>
                    </el-form-item>   
                    <el-button class="submit" type="primary" @click="resetPassword">提交</el-button>
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
    import NavBar from "../components/NavBar";
    import FlashMessage from "../components/FlashMessage";
    import FooterUltimate from "../components/FooterUltimate";
    import axios from "axios"

    export default {
        name: "ResetPassword",
        components: {FooterUltimate, FlashMessage, NavBar},
        methods:{
          resetPassword(){
                axios.post('/password',{
                  passwordData: this.passwordData,
                  token: this.$route.params.token
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
            var validatePass = (rule, value, callback) => {
                if (value === '') {
                callback(new Error('请输入密码'));
                } else {
                if (this.passwordData.confirmPassword !== '') {
                    this.$refs.passwordData.validateField('confirmPassword');
                }
                callback();
                }
            };
            var validatePass2 = (rule, value, callback) => {
                if (value === '') {
                callback(new Error('请再次输入密码'));
                } else if (value !== this.passwordData.newPassword) {
                callback(new Error('两次输入密码不一致!'));
                } else {
                callback();
                }
            }; 
          return{
                passwordData: {
                    email: '',
                    newPassword: '',
                    confirmPassword: ''
                },
                rulesPassword: {
                    email:　[
                        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                        { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                    ],
                    newPassword: [
                        { required: true, message: '密码不能为空', trigger: ['blur', 'change']},
                        { validator: validatePass, trigger: ['blur', 'change'] }
                    ],
                    confirmPassword: [
                        { required: true, message: '密码不能为空', trigger: ['blur', 'change']},
                        { validator: validatePass2, trigger: ['blur', 'change'] }
                    ],
                },
          }
        }
    }
</script>

<style scoped>

</style>

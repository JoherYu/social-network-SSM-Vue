<template>
  <div>
    <nav-bar></nav-bar>
    <main>
      <FlashMessage></FlashMessage>
      <div class="container">
        <div class="jumbotron">
          <div class="row">
            <div class="col-md-8">
              <img src="./../../static/index.jpg" class="rounded img-fluid">
            </div>
            <div class="col-md-4" v-if=" $store.state.Nav.IndexFlag == 'login' ">
                <div class="card mb-3 w-100 bg-light">
                    <div class="card-header"><h4>登录分相</h4></div>
                    <div class="card-body">
                        <form method="post" class="form" role="form">
                          <div class="form-group required"><label class="form-control-label" for="email">电子邮箱</label>
                            <input class="form-control" id="email" required type="text" v-model="Email">
                          </div>
                          <div class="form-group required"><label class="form-control-label" for="password">密码</label>
                            <input class="form-control" id="password" required type="password" v-model="password">
                          </div>
                          <div class="form-group form-check">
                            <label class="form-check-label">
                            <input class="form-check-input" id="remember_me" type="checkbox" v-model="Remember"> 记住我</label>
                          </div>
                          <el-button type="primary" @click="login">登录</el-button>
                        </form>
                        <hr>
                        <p class="small"><a href="javascript:void(0)" @click="ToRegister">注册新帐号</a></p>
                        <p class="small"><router-link to="/main/forget">忘记密码</router-link></p>
                    </div>
                </div>
            </div>
            <div class="col-md-4" v-else-if=" $store.state.Nav.IndexFlag == 'register' ">
                <div class="card mb-3 w-100 bg-light">
                    <div class="card-header"><h4>欢迎来到分相！</h4></div>
                    <div class="card-body">
                        <el-form label-position="top" label-width="80px" :model="authData" :rules="rulesAuth" >
                            <label class="form-control-label">姓名</label>
                            <el-form-item prop="name">
                            <el-input v-model="authData.name"></el-input>
                            </el-form-item>
                            <label class="form-control-label">用户名</label>
                            <el-form-item prop="username">                                    
                            <el-input v-model="authData.username"></el-input>
                            </el-form-item>
                            <label class="form-control-label">电子信箱</label>
                            <el-form-item prop="email">
                            <el-input v-model="authData.email"></el-input>
                            </el-form-item>
                            <label class="form-control-label">密码</label>
                            <el-form-item prop="newPassword">                                    
                            <el-input type="password" v-model="authData.newPassword"></el-input>
                            </el-form-item>
                            <label class="form-control-label">确认密码</label>
                            <el-form-item prop="confirmPassword">                                      
                            <el-input type="password" v-model="authData.confirmPassword"></el-input>
                            </el-form-item>                             
                            <el-button class="submit" type="primary" @click="register">注册</el-button>
                        </el-form>                         
                        <hr>
                        <small>已经拥有帐户? <a href="javascript:void(0)" @click="ToLogin">点击此处登录</a>
                        </small>
                    </div>
                </div>
            </div>
            <div class="col-md-4 align-self-center" v-else>
              <h1>分相</h1>
              <p>捕捉每一个精彩瞬间</p>
              <p><a class="btn btn-primary btn-lg" href="javascript:void(0)" @click="ToRegister">现在加入</a></p>
            </div>
          </div> 
        </div>
        <footer-ultimate></footer-ultimate>   
      </div>  
    </main>
  </div>
</template>

<script>
    import FooterUltimate from './../components/FooterUltimate'
    import NavBar from './../components/NavBar'
    import FlashMessage from './../components/FlashMessage'
    import axios from 'axios'
    export default {
        name: "Index",
        data(){
            let validUsername=(rule,value,callback)=>{
                let reg=/^[a-zA-Z0-9]*$/
                if(!reg.test(value)){callback(new Error('用户名应仅包含数字与大小写字母'))
                }else{
                    callback()
                }
            };
            var validatePass = (rule, value, callback) => {
                if (value === '') {
                callback(new Error('请输入密码'));
                } else {
                if (this.authData.confirmPassword !== '') {
                    this.$refs.authData.validateField('confirmPassword');
                }
                callback();
                }
            };
            var validatePass2 = (rule, value, callback) => {
                if (value === '') {
                callback(new Error('请再次输入密码'));
                } else if (value !== this.authData.newPassword) {
                callback(new Error('两次输入密码不一致!'));
                } else {
                callback();
                }
            }; 
          return{
            Email:'',
            password:'',
            Remember:false,
            authData: {
                name: '',
                username: '',
                email:'',
                newPassword: '',
                confirmPassword: ''
            },
            rulesAuth: {
                name: [
                    { required: true, message: '昵称不能为空', trigger: ['blur', 'change']}
                ],
                username: [
                    { required: true, message: '用户名不能为空', trigger: ['blur', 'change']},
                    { validator: validUsername, trigger: ['blur', 'change'] }
                ],
                email: [
                    { required: true, message: '请输入邮箱地址', trigger: ['blur', 'change'] },
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
        },
        mounted(){
          this.GetCSRF()
        },
        components:{
          FooterUltimate,
          NavBar,
          FlashMessage
        },
        methods:{
          register(){
            axios.post('/user', {
              authData: this.authData
            },{
              headers:{
                'X-CSRFToken': this.$store.state.Page.CSRFToken
              }
            }).then((result)=>{
              var res = result.data
              this.$store.commit('FlashMessage', res)
              if (res.type == "success"){
                this.Email = this.authData.email,
                this.password = this.authData.confirmPassword,
                this.Remember = false,
                axios({
                method:'post',
                url:'/session', 
                data:{
                  email:this.Email,
                  password:this.password,
                  rememberMe:this.Remember
                },
                headers:{
                  'X-CSRFToken': this.$store.state.Page.CSRFToken
                }
              }).then((result)=>{
                var res = result.data
                if (res.message){
                  this.$store.commit('FlashMessage', {
                    message:res.message,
                    type:res.type
                })
                if (res.type == 'success'){
                  var avatar_path_s = res.avataS
                  var avatar_path_m = res.avatarM
                  this.$store.commit('GetUserInfo', {
                    username:res.username, 
                    name:res.name,
                    avatar_path_s:avatar_path_s,
                    avatar_path_m:avatar_path_m, 
                    notification_count:res.notificationCount, 
                    can_moderate:res.canModerate,
                    can_comment:res.canComment,
                    remember_user: res.rememberMe,
                    is_admin: res.isAdmin
                    })
                
                  if (this.$store.state.Nav.RememberUser){
                    this.setLocalStorage("username",this.$store.state.Nav.username)
                    window.addEventListener("beforeunload",()=>{
                    localStorage.setItem("userMsg",JSON.stringify(this.$store.state))
                    })
                  }else{
                  sessionStorage.setItem("username",JSON.stringify(this.$store.state.Nav.username))                    
                  }
                  if (this.$store.state.Page.RedirectPage == '/'){
                    this.$router.push('/main/home')
                  }else{
                    this.$router.push(this.$store.state.Page.RedirectPage)
                  }
                }
                }
              })
              }
            })
          },
          ToLogin(){
            this.$store.commit('ChangeIndexFlag', 'login')
          },
          ToRegister(){
            this.$store.commit('ChangeIndexFlag', 'register')
          },          
          GetCSRF(){
            axios.get('/csrfToken').then((result)=>{
              var res = result.data
              var token = res.csrfToken
              this.$store.commit('StoreToken', token)
            })
          },
          login() {
            if (this.$store.state.Nav.HasLogin){
              this.$router.push('/main/home')
            }else {
              axios({
                method:'post',
                url:'/session', 
                data:{
                  email:this.Email,
                  password:this.password,
                  rememberMe:this.Remember
                },
                headers:{
                  'X-CSRFToken': this.$store.state.Page.CSRFToken
                }
              }).then((result)=>{
                var res = result.data
                if (res.message){
                  this.$store.commit('FlashMessage', {
                    message:res.message,
                    type:res.type
                })
                if (res.type == 'success'){
                  var avatar_path_s = res.avatarS
                  var avatar_path_m = res.avatarM
                  this.$store.commit('GetUserInfo', {
                    username:res.username, 
                    name:res.name,
                    avatar_path_s:avatar_path_s,
                    avatar_path_m:avatar_path_m, 
                    notificationCount:res.notificationCount, 
                    canModerate:res.canModerate,
                    canComment:res.canComment,
                    rememberMe: res.rememberMe,
                    isAdmin: res.isAdmin
                    })
                
                  if (this.$store.state.Nav.RememberUser){
                    this.setLocalStorage("username",this.$store.state.Nav.username)
                    window.addEventListener("beforeunload",()=>{
                    localStorage.setItem("userMsg",JSON.stringify(this.$store.state))
                    })
                  }else{
                  sessionStorage.setItem("username",JSON.stringify(this.$store.state.Nav.username))                    
                  }
                  if (this.$store.state.Page.RedirectPage == '/'){
                    this.$router.push('/main/home')
                  }else{
                    this.$router.push(this.$store.state.Page.RedirectPage)
                  }
                }
                }
              })
            }
          }
        }
    }
</script>

<style scoped>

</style>

<template>
    <div>
        <admin-nav></admin-nav>
        <div class="container">
            <flash-message></flash-message>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <el-breadcrumb separator-class="el-icon-arrow-right">
                    <el-breadcrumb-item :to="{ path: '/admin/manage' }">管理面板主页</el-breadcrumb-item>
                    <el-breadcrumb-item :to="{ path: '/admin/manage/user' }">管理用户</el-breadcrumb-item>
                    <el-breadcrumb-item>编辑个人资料</el-breadcrumb-item>
                    </el-breadcrumb>
                </ol>
            </nav>
            <div class="page-header">
                <h1>编辑个人资料
                    <router-link class="float-right btn btn-light" :to="'/main/user/' + profileData.username">返回用户主页</router-link>
                </h1>
            </div>
            <div class="card w-100 bg-light">
                <h3 class="card-header">编辑个人资料</h3>
                <div class="card-body">
                    <el-form label-position="top" label-width="80px" :model="profileData" :rules="rulesProfile" >
                        <label class="form-control-label">姓名</label>
                        <el-form-item prop="name">
                        <el-input v-model="profileData.name"></el-input>
                        </el-form-item>
                        <label class="form-control-label">用户名</label>
                        <el-form-item prop="username">                                    
                        <el-input v-model="profileData.username"></el-input>
                        </el-form-item>
                        <label class="form-control-label">网站</label>
                        <el-form-item>                                      
                        <el-input v-model="profileData.website"></el-input>
                        </el-form-item>   
                        <label class="form-control-label">城市</label>
                        <el-form-item>     
                        <el-input v-model="profileData.city"></el-input>
                        </el-form-item>   
                        <label class="form-control-label">自我介绍</label>
                        <el-form-item>   
                        <el-input v-model="profileData.bio" type="textarea"></el-input>
                        </el-form-item>
                        <label class="form-control-label">电子信箱</label>
                        <el-form-item prop="email">
                        <el-input v-model="profileData.email"></el-input>
                        </el-form-item>
                        <label class="form-control-label">用户分类</label><br>                    
                        <el-select v-model="value" placeholder="请选择">
                            <el-option
                            v-for="item in options"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                            </el-option>
                        </el-select><br><br>
                        <input type="checkbox" id="active" value="active" v-model="check">
                        <label for="active">未封禁</label><br>
                        <input type="checkbox" id="confirmed" value="confirmed" v-model="check">
                        <label for="confirmed">已确认</label><br>
                        <el-button class="submit" type="primary" @click="submit">提交</el-button>
                    </el-form> 
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import AdminNav from "../components/AdminNav"
    import FlashMessage from './../components/FlashMessage'
    import axios from 'axios'

    export default {
        name: "OthersSetting",
        components:{
          AdminNav,
          FlashMessage
        },
        mounted(){
            this.getData()
        },
        data(){
            let validUsername=(rule,value,callback)=>{
                let reg=/^[a-zA-Z0-9]*$/
                if(!reg.test(value)){callback(new Error('用户名应仅包含数字与大小写字母'))
                }else{
                    callback()
                }
            };            
            return{
                check:[],
                profileData: {
                    name: '',
                    username: '',
                    website: '',
                    city:'',
                    bio:'',
                    email:''
                },
                rulesProfile: {
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
                    ]
                },
                options: [{
                    value: 1,
                    label: '锁定'
                    }, {
                    value: 2,
                    label: '用户'
                    }, {
                    value: 3,
                    label: '协管员'
                    }, {
                    value: 4,
                    label: '管理员'
                    }],
                value: 2
            }
        },
        methods:{
            submit(){
                var confirmed, active
                if (this.check.indexOf('confirmed') == -1){
                    confirmed = false
                }else{
                    confirmed = true
                }
                if (this.check.indexOf('active') == -1){
                    active = false
                }else{
                    active = true
                }

                var check = {
                    confirmed:confirmed,
                    active:active
                }

                axios.put('/user/' + this.$route.params.id + '/adminSettings', {
                    profileData: this.profileData,
                    check: check,
                    role: this.value
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.$router.go(-1)
                })
            },
            getData(){
                axios.get('/user/' + this.$route.params.id + '/adminSettings').then((result)=>{
                    var res = result.data
                    this.profileData = res.profileData
                    this.check = res.check
                    this.value = res.value
                })
            }
        }      
    }
</script>

<style scoped>

</style>

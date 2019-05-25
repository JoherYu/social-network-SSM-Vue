<template>
    <div>
        <nav-bar></nav-bar>
        <main>
            <flash-message></flash-message>
            <div class="container">
                <div class="page-header">
                    <h1>设置</h1>
                </div>
                <div class="row">
                    <div class="col-md-3">
                        <div class="nav nav-pills flex-column" role="tablist" aria-orientation="vertical">
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'editProfile' && !($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('editProfile')">设置个人资料</a>
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'changeAvatar' || ($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('changeAvatar')">更改头像</a>
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'changePassword' && !($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('changePassword')">更改密码</a>
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'changeEmail' && !($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('changeEmail')">更改电子邮箱</a>
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'notification' && !($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('notification')">消息中心设置</a>
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'privacy' && !($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('privacy')">隐私设置</a>
                            <a class="nav-item nav-link " :class="{ active : ( navFlag == 'deleteAccount' && !($store.state.Page.EditAvaterFlag == 'changeAvatar') ) }" href="javascript:void(0)" @click="getData('deleteAccount')">删除帐号</a>
                        </div>
                    </div>  
                    <div class="col-md-9">
                        <div class="card w-100 bg-light" v-if="navFlag == 'editProfile'">
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
                                    <el-button class="submit" type="primary" @click="submitProfile">提交</el-button>
                                </el-form>                                                                   
                            </div>
                        </div> 
                        <div class="card w-100 bg-light" v-if="navFlag == 'changeAvatar'">
                            <h3 class="card-header">改变头像</h3>
                            <div class="card-body">
                                <el-upload
                                class="upload"
                                action="/avatar"
                                :before-remove="beforeRemove"
                                :on-success="changeImage"
                                :limit="1"
                                :on-exceed="handleExceed"
                                :headers="{ 'X-CSRFToken' : $store.state.Page.CSRFToken }"
                                :file-list="fileList">
                                <el-button size="small" type="primary">点击上传</el-button>
                                </el-upload>
                                <small class="text-muted">上传文件大小需小于3MB</small>
                            <div>
                                <img :src="avatarURL" id="crop-box" style="max-width: 500px; display: block;">
                                <div id="preview-box">
                                    <div class="preview-box" style="width: 200px; height: 200px; overflow: hidden;">
                                        <img :src="avatarURL" class="jcrop-preview" alt="Preview"/>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <form class="form" role="form">
                                    <input id="x" name="x" type="hidden" value="">
                                    <input id="y" name="y" type="hidden" value="">
                                    <input id="w" name="w" type="hidden" value="">
                                    <input id="h" name="h" type="hidden" value=""><br>
                                    <button class="btn btn-secondary" type="button" @click="cropImage">剪裁并上传头像</button>
                                </form>
                            </div>
                            </div>
                        </div>
                        <div class="card w-100 bg-light" v-if="navFlag == 'changePassword'">
                            <h3 class="card-header">更改密码</h3>
                            <div class="card-body">
                                <el-form label-position="top" label-width="80px" :model="passwordData" :rules="rulesPassword" >
                                    <label class="form-control-label">旧密码</label>
                                    <el-form-item prop="oldPassword">
                                    <el-input type="password" v-model="passwordData.oldPassword"></el-input>
                                    </el-form-item>
                                    <label class="form-control-label">新密码</label>
                                    <el-form-item prop="newPassword">                                    
                                    <el-input type="password" v-model="passwordData.newPassword"></el-input>
                                    </el-form-item>
                                    <label class="form-control-label">确认密码</label>
                                    <el-form-item prop="confirmPassword">                                      
                                    <el-input type="password" v-model="passwordData.confirmPassword"></el-input>
                                    </el-form-item>   
                                    <el-button class="submit" type="primary" @click="changePassword">提交</el-button>
                                </el-form> 
                            </div>
                        </div>
                        <div class="card w-100 bg-light" v-if="navFlag == 'changeEmail'">
                            <h3 class="card-header">更改电子邮箱</h3>
                            <div class="card-body">
                                <el-form label-position="top" label-width="80px" :model="emailData" :rules="rulesEmail" >
                                    <label class="form-control-label">新邮箱地址</label>
                                    <el-form-item prop="email">
                                    <el-input v-model="emailData.email"></el-input>
                                    </el-form-item>
                                    <el-button class="submit" type="primary" @click="changeEmail">提交</el-button>
                                </el-form> 
                            </div>
                        </div>                                                    
                        <div class="card w-100 bg-light" v-if="navFlag == 'notification'">
                            <h3 class="card-header">消息中心设置</h3>
                            <div class="card-body">
                                <input type="checkbox" id="newComment" value="newComment" v-model="checkedNotification">
                                <label for="newComment">新评论</label><br>
                                <input type="checkbox" id="newFollower" value="newFollower" v-model="checkedNotification">
                                <label for="newFollower">新粉丝</label><br>
                                <input type="checkbox" id="newCollector" value="newCollector" v-model="checkedNotification">
                                <label for="newCollector">新收藏</label><br>
                                <el-button class="submit" type="primary" @click="notificationChange">提交</el-button>                      
                            </div>
                        </div>   
                        <div class="card w-100 bg-light" v-if="navFlag == 'privacy'">
                            <h3 class="card-header">隐私设置</h3>
                            <div class="card-body">
                                <input type="checkbox" id="publicMyCollection" value="publicMyCollection" v-model="checkedPrivacy">
                                <label for="publicMyCollection">公开我的收藏</label><br>
                                <el-button class="submit" type="primary" @click="privacyChange">提交</el-button>                     
                            </div>
                        </div>  
                        <div class="card w-100 bg-light" v-if="navFlag == 'deleteAccount'">
                            <h3 class="card-header">注销账户</h3>
                            <div class="card-body">
                                <el-form label-position="top" label-width="80px" :model="cancelData" :rules="rulesCancel" >
                                    <label class="form-control-label">用户名</label>
                                    <el-form-item prop="username">                                    
                                    <el-input v-model="cancelData.username"></el-input>
                                    </el-form-item> 
                                    <el-button class="submit" type="primary" @click="deleteAccount">确定</el-button>
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
    import NavBar from "./../components/NavBar";
    import FlashMessage from "./../components/FlashMessage";
    import FooterUltimate from "./../components/FooterUltimate";
    import axios from 'axios';
    import './../../node_modules/jcrop/js/jquery.jcrop.js';
    import './../../node_modules/jcrop/css/jquery.jcrop.css';

    export default {
        name: "Setting",
        components:{
            NavBar,
            FlashMessage,
            FooterUltimate
        },
        mounted(){
            if (this.$store.state.Page.EditAvaterFlag == 'changeAvatar'){
                this.navFlag = 'changeAvatar'
                this.$store.commit('ChangeEditAvaterFlag', '')
            }
            if (this.$store.state.Page.EditSettingFlag == 'notification'){
                this.navFlag = 'notification'
                this.$store.commit('ChangeEditSettingFlag', '')
            }
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
                fileList: [],
                navFlag: 'editProfile',
                avatarURL: "./../../static/default_l.jpg",
                profileData: {
                    name: '',
                    username: '',
                    website: '',
                    city:'',
                    bio:''
                },
                passwordData: {
                    oldPassword: '',
                    newPassword: '',
                    confirmPassword: ''
                },
                emailData: {
                    email:''
                },
                cancelData: {
                    username:''
                },
                checkedNotification : [],
                checkedPrivacy: [],
                rulesProfile: {
                    name: [
                        { required: true, message: '昵称不能为空', trigger: ['blur', 'change']}
                    ],
                    username: [
                        { required: true, message: '用户名不能为空', trigger: ['blur', 'change']},
                        { validator: validUsername, trigger: ['blur', 'change'] }
                    ],
                },
                rulesPassword: {
                    oldPassword: [
                        { required: true, message: '密码不能为空', trigger: ['blur', 'change']}
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
                rulesEmail: {
                    email:　[
                        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                        { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                    ]
                },
                rulesCancel: {
                    username: [
                        { required: true, message: '用户名不能为空', trigger: ['blur', 'change']},
                        { validator: validUsername, trigger: ['blur', 'change'] }
                    ],
                },                
            }
        },
        methods:{
            getData(flag){
                if (flag){
                    this.navFlag = flag
                }
                if (this.navFlag == 'editProfile'){
                    axios.get('/user/profileSetting').then((result)=>{
                        var res = result.data
                        this.profileData = res
                    })
                }else if(this.navFlag == 'notification'){
                    axios.get('/user/notificationSetting').then((result)=>{
                        var res = result.data
                        this.checkedNotification = res.checkedNotification
                    })                    
                }else if(this.navFlag == 'privacy'){
                    axios.get('/user/privacySetting').then((result)=>{
                        var res = result.data
                        this.checkedPrivacy = res.checkedPrivacy
                    })    
                }else if(this.navFlag == 'changeAvatar'){               
                    $(function ($) {
                    var jcrop_api,
                        boundx,
                        boundy,

                        $preview = $('#preview-box'),
                        $pcnt = $('#preview-box .preview-box'),
                        $pimg = $('#preview-box .preview-box img'),

                        xsize = $pcnt.width(),
                        ysize = $pcnt.height();

                    $('#crop-box').Jcrop({
                        onChange: updatePreview,
                        onSelect: updateCoords,
                        setSelect: [0, 0, 200, 200],
                        aspectRatio: 1
                    }, function () {
                        var bounds = this.getBounds();
                        boundx = bounds[0];
                        boundy = bounds[1];

                        jcrop_api = this;
                        
                        jcrop_api.focus();

                        $preview.appendTo(jcrop_api.ui.holder);
                    });

                    function updatePreview(c) {
                        if (parseInt(c.w) > 0) {
                        var rx = xsize / c.w;
                        var ry = ysize / c.h;
                        $pimg.css({
                            width: Math.round(rx * boundx) + 'px',
                            height: Math.round(ry * boundy) + 'px',
                            marginLeft: '-' + Math.round(rx * c.x) + 'px',
                            marginTop: '-' + Math.round(ry * c.y) + 'px'
                        });
                        }
                    }
                    });

                    function updateCoords(c) {
                    $('#x').val(c.x);
                    $('#y').val(c.y);
                    $('#w').val(c.w);
                    $('#h').val(c.h);
                    }
                }
            },
            submitProfile(){
                axios.put('/user/profileSetting', {
                    profileData: this.profileData
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.$store.commit('UpdateProfile', {username:this.profileData.username, name:this.profileData.name})
                    this.$router.push('/main/user/' + this.$store.state.Nav.username)
                })
            },
            handleExceed(files, fileList) {
                this.$message.warning('请进行头像剪裁上传或移除当前图片后进行重新上传');
            },
            beforeRemove(file, fileList) {
                return this.$confirm(`确定移除 ${ file.name }？`);
            },
            changeImage(res){
                if (res.message){
                    this.$store.commit('FlashMessage', { message:res.message, type:res.type })
                }
                this.avatarURL = res.url
                this.navFlag = 'editProfile'  //因插件在vue中使用存在未知错误，故有此处操作，以保证图片显示效果
                setTimeout(() =>{
                this.getData('changeAvatar')
                },1);
            },
            cropImage(){
                var x = $('#x').val()
                var y = $('#y').val()
                var w = $('#w').val()
                var h = $('#h').val()
                axios.put('/thumbnail', {
                    x:x,
                    y:y,
                    w:w,
                    h:h
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', { message:res.message, type:res.type })
                    this.$store.commit('UpdateAvater', { avatarS:res.avatarS, avatarM:res.avatarM})
                    if (res.type == 'success'){
                        this.$router.push('/main/user/' + this.$store.state.Nav.username)
                    }

                })
            },
            changePassword(){
                axios.put('/user/password', {
                    oldPassword:this.passwordData.oldPassword,
                    password:this.passwordData.newPassword
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if (res.type == 'success'){
                        this.$router.push('/main/user/' + this.$store.state.Nav.username)
                    }                   
                })
            },
            changeEmail(){
                axios.put('/user/email', {
                    email:this.emailData.email
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.$router.push('/main/user/' + this.$store.state.Nav.username)
                })
            },
            notificationChange(){
                var newComment, newFollower, newCollector
                if (this.checkedNotification.indexOf('newComment') == -1){
                    newComment = false
                }else{
                    newComment = true
                }
                if (this.checkedNotification.indexOf('newFollower') == -1){
                    newFollower = false
                }else{
                    newFollower = true
                }
                if (this.checkedNotification.indexOf('newCollector') == -1){
                    newCollector = false
                }else{
                    newCollector = true
                }
                axios.put('/user/notificationSetting', {
                    newCollector:newCollector,
                    newComment:newComment,
                    newFollower:newFollower
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.$router.push('/main/user/' + this.$store.state.Nav.username)
                })
            },
            privacyChange(){
                var publicMyCollection
                if (this.checkedPrivacy.indexOf('publicMyCollection') == -1){
                    publicMyCollection = false
                }else{
                    publicMyCollection = true
                }
                axios.put('/user/privacySetting', {
                    publicMyCollection:publicMyCollection
                },{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    this.$router.push('/main/user/' + this.$store.state.Nav.username)
                })
            },
            deleteAccount(){
                axios.delete('/user/' + this.cancelData.username,{
                    headers:{
                        'X-CSRFToken': this.$store.state.Page.CSRFToken
                    }
                }).then((result)=>{
                    var res = result.data
                    this.$store.commit('FlashMessage', res)
                    if (res.type == 'success'){
                        sessionStorage.clear()
                        localStorage.clear()
                        this.$store.commit('Reset')
                        this.$router.push('/')
                    }             
                })
            }            
        }
        
    }
</script>

<style scoped>

</style>

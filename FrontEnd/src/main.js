// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import Vuex from 'vuex'
import {setLocalStorage, getLocalStorage} from './resource/utils'
import moment from 'moment'
import $ from 'jquery'
import axios from 'axios'

axios.defaults.withCredentials=true

Vue.prototype.setLocalStorage = setLocalStorage
Vue.prototype.getLocalStorage = getLocalStorage
Vue.prototype.$moment = moment

moment.locale('zh-cn')

Vue.config.productionTip = false

Vue.use(ElementUI);
Vue.use(Vuex);

const moduleNav = {
  state:{
    IndexFlag:'',
    username:'',
    name:'',
    AvatarPathS:'',
    AvatarPathM:'',
    NotificationCount:0,
    CanModerate:false,
    CanComment:true,
    HasLogin:false,
    query:'',
    SearchLoading:true,
    SearchItems:{},
    SearchItemsCount:0,
    SearchItemsCurrentPage:1,
    SearchItemsPerPage:10,
    RememberUser:false,
    IsAdmin:false
  },
  mutations:{
    ClearNotificationCount(state){
      state.NotificationCount = 0
    },
    DecreaseNotificationCount(state){
      state.NotificationCount--
    },
    ChangeIndexFlag(state, flag){
      state.IndexFlag = flag
    },
    GetUserInfo(state, data){
      state.username = data.username;
      state.name = data.name;
      state.AvatarPathS = data.avatar_path_s;
      state.AvatarPathM = data.avatar_path_m;
      state.NotificationCount = data.notificationCount;
      state.CanModerate = data.canModerate;
      state.CanComment = data.canComment;
      state.HasLogin = true
      state.RememberUser= data.rememberMe
      state.IsAdmin = data.isAdmin
    },
    ChangeLoginFlag(state){
      state.HasLogin = true
    },
    SendQueryParam(state, query){
      state.query = query
    },
    SendSearchData(state, data){
      state.SearchItems = data.items
      state.SearchItemsCount = data.count
      state.SearchItemsCurrentPage = data.currentPage,
      state.SearchItemsPerPage = data.perPage
    },
    SearchLoadingChange(state){
      state.SearchLoading = false
    },
    UpdateProfile(state, data){
      state.username = data.username;
      state.name = data.name;
    },
    UpdateAvater(state, data){
      state.AvatarPathS = data.avatarS;
      state.AvatarPathM = data.avatarM;      
    },
    Reset(state){
      state.IndexFlag='',
      state.username='',
      state.name='',
      state.AvatarPathS='',
      state.AvatarPathM='',
      state.NotificationCount=0,
      state.CanModerate=false,
      state.CanComment=true,
      state.HasLogin=false,
      state.query='',
      state.SearchLoading=true,
      state.SearchItems={},
      state.SearchItemsCount=0,
      state.SearchItemsCurrentPage=1,
      state.SearchItemsPerPage=10,
      state.RememberUser=false,
      state.IsAdmin=false      
    }
  }
}

const moduleMessage = {
  state:{
    message:'',
    MessageType:'',
  },
  mutations:{
    FlashMessage(state, data){
      state.message = data.message;
      state.MessageType = data.type;
    },
  }
}

const modulePage = {
  state:{
    CSRFToken:'',
    SearchCategory:'photo',
    RedirectPage:'/',
    EditAvaterFlag: '',
    EditSettingFlag: '',
    ManageFilter:''
  },
  mutations:{
    StoreToken(state, token){
      state.CSRFToken = token
    },
    SearchCategory(state, category){
      state.SearchCategory = category
    },
    UpdateRedirectPage(state, page){
      state.RedirectPage = page
    },
    ChangeEditAvaterFlag(state, flag){
      state.EditAvaterFlag = flag
    },
    ChangeEditSettingFlag(state, flag){
      state.EditSettingFlag = flag
    },
    ChangeManageFilter(state, rule){
      state.ManageFilter = rule
    }
  }
}

const moduleSearchPagination = {
  state:{
    CurrentPage:1
  },
  mutations:{
    ResetPage(state, page){
      state.CurrentPage = page
    },
    IncreasePage(){
      state.CurrentPage++
    },
    DecreasePage(){
      state.CurrentPage--
    }
  }
}

const modulePhoto = {
  state:{
    photo:{},
    comments:{},
    page:1,
    pagination:{},
    description:'',
    tags:[],
    isCollected:false,
    canComment:false
  },
  mutations:{
    pageData(state, data){
      state.photo = data.photo
      state.comments = data.comments
      state.page = data.pagination.currentPage
      state.pagination = data.pagination
      state.description = data.photo.description
      state.tags = data.photo.tags
      state.isCollected = data.photo.collected
      state.canComment = data.photo.canComment
    },
    changeDescription(state, description){
      state.description = description
    },
    changeTags(state, tags){
      state.tags = tags
    },
    changeCollectFlag(state, collectFlag){
      state.isCollected = collectFlag
    },
    changePage(state, page){
      state.page = page
    },
    changeCommentFlag(state, commentFlag){
      state.canComment= commentFlag
    },
  }
}

const store = new Vuex.Store({
  modules:{
    Nav:moduleNav,
    Message:moduleMessage,
    Page:modulePage,
    SearchPagination:moduleSearchPagination,
    Photo:modulePhoto
  }
})

$("[data-toggle='tooltip']").tooltip({title: moment($(this).data('timestamp')).format('lll')})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})

export default store
import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/views/Index'
import Explore from '@/views/Explore'
import Search from '@/views/Search'
import Home from '@/views/Home'
import store from'@/main'
import Upload from '@/views/Upload'
import Photo from '@/views/Photo'
import User from '@/views/User'
import Setting from '@/views/Setting'
import OthersSetting from '@/views/OthersSetting'
import Notification from '@/views/Notification'
import Tag from '@/views/Tag'
import Collector from '@/views/Collector'
import Manage from '@/views/Manage'
import ManagePhoto from '@/views/ManagePhoto'
import ManageUser from '@/views/ManageUser'
import ManageComment from '@/views/ManageComment'
import ManageTag from '@/views/ManageTag'
import ResetPassword from '@/views/ResetPassword'
import ForgetPassword from '@/views/ForgetPassword'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Index',
      component: Index,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next({path:'/main/home'})
        }else{
          next()
        }
      }
    },
    {
      path: '/main/explore',
      name: 'Explore',
      component: Explore
    },
    {
      path: '/main/upload',
      name: 'Upload',
      component: Upload,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next()
        }else{
          store.commit('UpdateRedirectPage', to)
          next({path:'/'})
        }
      }
    },
    {
      path: '/main/home',
      name: 'Home',
      component: Home,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next()
        }else{
          store.commit('UpdateRedirectPage', to)
          next({path:'/'})
        }
      }
    },
    {
      path: '/main/search',
      name: 'Search',
      component: Search
    },
    {
      path: '/main/photo/:id',
      name: 'Photo',
      component: Photo
    },
    {
      path: '/main/user/:username',
      name: 'User',
      component: User
    },
    {
      path: '/main/follower/:username',
      name: 'User',
      component: User,
      beforeEnter: (to, from, next)=>{
          store.commit('UpdateRedirectPage', '/main/follower')
          next()
        }
    },
    {
      path: '/main/setting',
      name: 'Setting',
      component: Setting,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next()
        }else{
          store.commit('UpdateRedirectPage', to)
          next({path:'/'})
        }
      }
    },
    {
      path: '/main/setting/:id',
      name: 'OthersSetting',
      component: OthersSetting,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next()
        }else{
          store.commit('UpdateRedirectPage', to)
          next({path:'/'})
        }
      }
    },
    {
      path: '/main/notifications',
      name: 'Notification',
      component: Notification,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next()
        }else{
          store.commit('UpdateRedirectPage', to)
          next({path:'/'})
        }
      }
    },
    {
      path: '/main/tag/:id',
      name: 'Tag',
      component: Tag,
      beforeEnter: (to, from, next)=>{
        if ( sessionStorage.getItem("username") ||  localStorage.getItem("username") ){
          next()
        }else{
          store.commit('UpdateRedirectPage', to)
          next({path:'/'})
        }
      }
    },
    {
      path: '/main/collectors/:id',
      name: 'Collector',
      component: Collector,
    },
    {
      path: '/admin/manage',
      name: 'Manage',
      component: Manage
    },
    {
      path: '/admin/manage/photo',
      name: 'ManagePhoto',
      component: ManagePhoto
    },
    {
      path: '/admin/manage/user',
      name: 'ManageUser',
      component: ManageUser
    },
    {
      path: '/admin/manage/comment',
      name: 'ManageComment',
      component: ManageComment
    },
    {
      path: '/admin/manage/tag',
      name: 'ManageTag',
      component: ManageTag
    },
    {
      path: '/main/forget',
      name: 'ForgetPassword',
      component: ForgetPassword
    },
    {
      path: '/main/reset/:token',
      name: 'ResetPassword',
      component: ResetPassword
    },
  ]
})

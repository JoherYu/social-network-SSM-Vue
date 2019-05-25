<template>
    <div>
      <div class="card bg-light mb-3 w-100 sidebar-nav">
        <div class="card-body">
          <div class="row">
            <router-link :to="'/main/user/' + $store.state.Nav.username">
              <img class="rounded avatar-s" style="margin-left: 15px; position: absolute"
                   :src="$store.state.Nav.AvatarPathM">
            </router-link>
            <div class="sidebar-nav-name">
              <h6 class="card-title"><router-link
                :to="'/main/user/' + $store.state.Nav.username">{{ $store.state.Nav.name }}
              </router-link></h6>
              <p class="card-subtitle mb-2 text-muted">{{ $store.state.Nav.username }}</p>
            </div>
          </div>
          <router-link class="card-link" :to="'/main/user/' + $store.state.Nav.username">
            <span class="oi oi-home"></span> 我的相册
          </router-link><br>
          <a class="card-link" href="javascript:void(0)" @click="toCollect">
            <span class="oi oi-star"></span> 我的收藏
          </a>
        </div>
      </div>
      <div class="card bg-light mb-3 w-100">
        <div class="card-header">热门标签</div>
        <div class="list-group">
          <div v-for="tag in tags" :key="tag.id">
          <router-link class="list-group-item" :to="'/main/tag/' + tag.id">{{ tag.name }}
            <span class="badge badge-pill">{{ tag.photoCount }}</span>
          </router-link>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
    export default {
        name: "HomeSideBar",
        props:['tags'],
        methods:{
          toCollect(){
            this.$store.commit('UpdateRedirectPage', this.$route.path)
            this.$router.push('/main/user/' + this.$store.state.Nav.username)
          }
        }
    }
</script>

<style scoped>

</style>

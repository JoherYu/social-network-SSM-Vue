<template>
  <div v-if="$store.state.Message.message" class="bottom">
    <el-alert
      :title="$store.state.Message.message"
      :type="$store.state.Message.MessageType"
      @close="HideMessage"
      center
      show-icon>
    </el-alert>
  </div>
</template>

<script>
    export default {
        name: "FlashMessage",
        methods:{
          HideMessage(){
            this.$store.commit('FlashMessage', {message:'', type:""})
          }
        }
    }
</script>

<style scoped>
.bottom{margin-bottom:10px}
</style>

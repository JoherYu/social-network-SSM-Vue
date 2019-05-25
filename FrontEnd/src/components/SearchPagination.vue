<template>
  <div class="page-footer">
    <el-pagination
      background
      layout="prev, pager, next"
      :total="$store.state.Nav.SearchItemsCount"
      :current-page="$store.state.Nav.SearchItemsCurrentPage"
      :page-size="$store.state.Nav.SearchItemsPerPage"
      @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'>
    </el-pagination>
  </div>
</template>

<script>
    export default {
        name: "SearchPagination",
        methods:{
          ChangePage(val){
            this.$emit('current-change', val)
          },
          PrePage(){
            this.$emit('prev-click')
          },
          NextPage(){
            this.$emit('next-click')
          }
        }
    }
</script>

<style scoped>

</style>

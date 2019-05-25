<template>
  <div class="page-footer">
    <el-pagination
      background
      layout="prev, pager, next"
      :total="pagination.count"
      :current-page="pagination.currentPage"
      :page-size="pagination.perPage"
      @current-change='ChangePage' @prev-click='PrePage' @next-click='NextPage'>
    </el-pagination>
  </div>
</template>

<script>
    export default {
        name: "pagination",
        props:['pagination'],
        methods: {
          ChangePage(val) {
            this.$emit('current-change', val)
          },
          PrePage() {
            this.$emit('prev-click')
          },
          NextPage() {
            this.$emit('next-click')
          }
        }
    }
</script>

<style scoped>

</style>

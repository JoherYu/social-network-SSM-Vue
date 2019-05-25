<template>
    <div>
      <nav-bar></nav-bar>
      <main>
        <div class="container">
          <div class="page-header">
              <h1>上传相片</h1>
          </div>
          <div class="row">
              <div class="col-md-12">
                  <vue-dropzone ref="myVueDropzone" id="dropzone" :options="dropzoneOptions"></vue-dropzone><br>
                  <router-link class="btn btn-light float-right" :to="'/main/user/' + $store.state.Nav.username">
                      前往我的主页
                  </router-link>
              </div>
          </div>
          <footer-ultimate></footer-ultimate>
        </div>
      </main>
    </div>
</template>

<script>
    import vue2Dropzone from 'vue2-dropzone'
    import 'vue2-dropzone/dist/vue2Dropzone.min.css'
    import NavBar from './../components/NavBar'
    import FooterUltimate from './../components/FooterUltimate'

    export default {
        name: "Upload",
        components: {
          vueDropzone: vue2Dropzone,
          NavBar,
          FooterUltimate
        },
        data(){
          return {
            dropzoneOptions: {
              url: '/photo',
              headers: {'X-CSRFToken': this.$store.state.Page.CSRFToken},
              uploadMultiple: false,
              parallelUploads: 2,
              paramName: "file",
              maxFilesize: 3,
              acceptedFiles: "image/*",
              maxFiles: 30,
              dictDefaultMessage: `拖拽文件或单击此处上传`,
              dictFallbackMessage: "当前浏览器不支持拖拽上传",
              dictInvalidFileType: "不能上传此文件类型",
              dictFileTooBig: "文件过大，上传文件最大不应超过3MB",
              dictResponseError: "服务错误",
              dictMaxFilesExceeded: "不能上传更多的文件"
            }
          }
        }
    }
</script>

<style scoped>
  .dropzone{margin: 20px 0; border: 2px dashed #0087F7; min-height: 400px;}
</style>

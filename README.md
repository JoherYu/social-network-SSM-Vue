# 基于SSM框架下的前后端分离的单体Web项目
本项目为第二次改写项目[分相](https://github.com/JoherYu/flask_web_program_social_network)，在项目[Vue-Flask-Website](https://github.com/JoherYu/Vue-Flask-Website)的基础上将后端语言替换为Java。
后端采用SSM框架，前端继续使用VUE进行开发，并采用以前端接口为主的RESTful风格的API进行前后端数据交互，同时修复一些前端布局问题。
### 本项目从Flask迁移的各种功能替换方案如下：  
*    **虚拟数据的生成**    

     结合[java-faker](https://github.com/DiUS/java-faker)和[awesome-identicon](https://github.com/superhj1987/awesome-identicon)编写脚本生成[虚拟数据](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/BackEnd/src/main/java/com/sharephoto/service/GenerateFakeDataImpl.java)，
     [虚拟图片](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/BackEnd/src/main/java/com/sharephoto/utils/GenerateImage.java)采用原生IO编写生成。
     虚拟数据通过调用[WebAPI](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/BackEnd/src/main/java/com/sharephoto/controller/FakingItController.java)生成。为安全起见，虚拟数据只能在新建数据库中生成（[MySQL](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/share_photo.sql)），否则将生成失败。
*    **用户注册预处理**    

     注册时，自我关注记录使用触发器（share_photo.sql:150）生成，默认头像调用[GenerateAvatar](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/BackEnd/src/main/java/com/sharephoto/utils/GenerateAvatar.java)生成。
*    **文件冗余处理**    

     图片直接调用原生IO删除，头像通过session传递需要删除的图像信息（UserServiceImpl.java:88-180-191）再进行删除,以代替SQLalchemy的数据库监听功能。
*    **安全管理**    

     使用Shiro框架进行登录及角色权限认证管理，使用SpringMVC拦截器结合注解进行[用户激活状态认证](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/BackEnd/src/main/java/com/sharephoto/utils/confirmAnnotationInterceptor.java)和[CSRF认证](https://github.com/JoherYu/social-network-SSM-Vue/blob/master/BackEnd/src/main/java/com/sharephoto/utils/CSRFAnnotationInterceptor.java)。
     使用JJWT和Java-Mail实现电子信箱激活、密码重置与邮箱更换功能。
*    **其他**    

     分页功能使用PageHelpert替代，图片处理使用原始IO编写。

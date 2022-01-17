## Less Player 是一款基于JavaFX开发的“轻量级本地”音乐播放器
### 欢迎喜欢GUI开发的朋友一起学习和交流

#### 界面参考
* Dopamine 3 (开源)  
   [官方网站 https://www.digimezzo.com/](https://www.digimezzo.com/)  
   [Github源码 https://github.com/digimezzo/dopamine](https://github.com/digimezzo/dopamine)  
   
* Right-Player (C++ Qt)  
   作者: MrBeanCpp（B站UP主）  
   [Github源码 https://github.com/MrBeanCpp/Right-Player](https://github.com/MrBeanCpp/Right-Player)
* 腾讯QQ音乐(迷你模式)

#### 开发环境
* Windows平台
* JDK版本: 1.8.0_291
* IDE: Eclipse 2021-09 (4.21.0)
* 依赖(开源)库: Jaudiotagger、jFlac、JAAD、Jorbis

#### 目前功能
* 播放器基本功能: 播放/暂停、上/下一首、进度条/播放时间、播放模式、音量控制等
* 支持音频类型: mp3、flac、ogg、wav、aac、m4a
* 界面: 主界面(包括歌曲信息、频谱、进度条、控制按钮等)、当前播放(列表)、歌词界面
* 风格: 支持2套简约风格界面，可在运行后随意切换
* 动画: 在迷你风格界面下，歌曲专辑封面可支持自动旋转动画
* PS: flac和ogg暂时不支持进度控制，部分wav可能不能播放(API受限)

#### 界面预览  
* 普通风格  
![snap 01.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2001.png)  
![snap 02.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2002.png)   
![snap 03.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2003.png)  
![snap 04.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2004.png)  
* 迷你风格 
![snap 05.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2005.png)  
![snap 06.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2006.png)  
![snap 07.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2007.png)  

#### 开发说明
目前版本将拆分模块，进行模块化开发，暂时不考虑引入Maven/Gradle等
##### 源码目录
* libs: 依赖库目录(第三方依赖)
* src: 主模块源码根目录
* Less-Player-xxx: 其他模块根目录

##### 模块依赖
PS: A -> B, 表示: A 依赖 B
* 主模块 -> Less-Player-Api

#### 其他
* Release版本中所使用的.exe文件打包工具: exe4j  

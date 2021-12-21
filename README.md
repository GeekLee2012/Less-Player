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
* 依赖(开源)库: Jaudiotagger、jFlac

#### 目前功能
* 基于JavaFX默认播放器API，实现最基本的播放器功能: 播放、暂停、上/下一首、进度条/播放时间、播放模式、音量控制等
* 支持播放音频类型: mp3、m4a、wav(部分)、mp4(仅音频)
* 界面: 播放器主界面(包括频谱、歌曲信息)、当前播放(列表)界面、歌词界面
* 其他: 可选2套简约风格界面(暂时不支持运行后随意切换，仅可运行时通过入参切换)

#### 界面预览  
风格1:  
![snap 01.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2001.png)  
![snap 02.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2002.png)   
![snap 03.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2003.png)  
![snap 04.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2004.png)  
风格2:  
![snap 05.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2005.png)  
![snap 06.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2006.png)  
![snap 07.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2007.png)  

#### 界面风格切换
无参数: 普通风格(即界面预览中的风格1)  
参数-mini(不区分大小写)：Mini风格(即界面预览中的风格2)  
![snap 00.png](https://github.com/GeekLee2012/Less-Player/blob/main/snapshot/snap%2000.png)  

#### 其他
* Release版本中所使用的.exe文件打包工具: exe4j  

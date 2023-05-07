# Less Player 
一款基于JavaFX开发的“轻量级本地”音乐播放器。  
  
<b>项目目前已停止更新</b>，原因：Java现有音频库，无法很好满足播放需求。

如果你也平时工作生活中，喜欢听听音乐、广播，  
欢迎体验，我的另一款在线播放器：  
基于Electron + Vue3开发的“同名款”：[Less-Player-Desktop](https://github.com/GeekLee2012/Less-Player-Desktop)  
  
### 欢迎喜欢GUI开发的朋友一起学习、交流

#### 界面参考
* Dopamine 3 （开源）  
   [官方网站 https://www.digimezzo.com/](https://www.digimezzo.com/)  
   [Github源码 https://github.com/digimezzo/dopamine](https://github.com/digimezzo/dopamine)  
   
* Right-Player （C++ Qt）  
   作者: MrBeanCpp（B站UP主）  
   [Github源码 https://github.com/MrBeanCpp/Right-Player](https://github.com/MrBeanCpp/Right-Player)
* 腾讯QQ音乐（迷你模式）

#### 开发环境
* OS: Windows 7、macOS Big Sur
* JDK版本: 1.8.0_301（x86版本，即32bit版本）
* IDE: IntelliJ IDEA 2021.3.2 CE、Eclipse 2021-12 (4.22.0)
* libs（第三方依赖库）: Jaudiotagger、jFlac、JAAD、Jorbis

#### 主要功能
* 播放器基本功能: 播放/暂停、上/下一首、进度条/播放时间、播放模式、音量控制等
* 支持音频类型: mp3、flac、ogg、wav、aac、m4a
* 界面: 主界面（包括歌曲信息、频谱、进度条、控制按钮等）、当前播放界面、歌词界面
* 风格: 支持2套简约风格界面，运行后可随意切换
* 动画: 在迷你风格界面，歌曲专辑封面可自动旋转
* 频谱: 在普通风格界面，支持 4种频谱（ 初级）样式；同时支持在指定区域双击鼠标进行切换

###### PS
* 由于API受限（能力有限），flac音频、ogg音频暂时不支持进度控制和频谱功能
* 由于API受限（能力有限），部分wav音频、mp3音频（主要为macOS平台）可能播放失败（高品质音频大概率播放失败）
* 频谱切换方式: 双击鼠标（左/右键均可以）
* 切换至上一个频谱: 普通风格界面中，在频谱区域的正左边（即歌曲专辑封面的位置）双击鼠标
* 切换至下一个频谱: 普通风格界面中，在频谱区域双击鼠标

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

#### 开发者说
目前版本进行模块化开发，但暂时不考虑引入Maven/Gradle等  
PS: 项目开发（bushi）计划 ，请参考TODO.md

###### 源码目录
* libs: （第三方）依赖库根目录
* src: 主模块源码根目录
* Less-Player-xxx: 其他模块根目录

###### 模块依赖
PS: A -> B, 表示: A 依赖 B
* 主模块 -> Less-Player-Api

#### 其他

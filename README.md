# LuckCat图床管理系统

> ## 环境需求
- java - 8
- mysql - 5.7.39
- Node.js - 16.17
- redis - 6.2.7
- minio - 最新版

> ### 使用方法
1. 根据版本安装minio服务和redis服务，并且根据以下注意事项修改相关配置
2. 修改application中的mail设置`host`、`username`、`password`
3. 运行压缩包中的start.bat文件
4. 访问`localhost:1216`开启服务，默认第一次注册用户为admin用户
> ### Minio设置
- 启动后创建`photo`和`avatar`两个bucket全部设置为public
- 用户名和密码设置为 `minioadmin`

> ### Redis设置
- 默认的密码设置为`123456`保险起见请为自己的redis设置密码
- 端口`6379`
- 如果需要更改请在application中修改

> ### Mysql设置
- Mysql数据库端口为默认：`3306`
- 默认用户名与密码为：`username: luckcat`、`password: root`

> ### Application修改
- email服务需要在application中修改，位置位于压缩包根目录
- 其余设置按需修改，如redis和minio服务的相关修改
- 请不要随意修改sa-token相关设置，以免造成无法使用情况

> ### 注意事项
- 默认情况下请按照application相关设置修改您所启动的服务
- 数据库中的Oss设置目前之后minio后续会增加其他oss服务，所以不要删除application中的minio设置
- Mysql数据库端口为默认：`3306`

> ### 感谢您的使用
创作者： Oriental、ming521、SaltedFish
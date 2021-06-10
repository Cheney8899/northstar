# Northstar程序化交易平台
基于JAVA的程序化交易平台: Northstar  
本项目基于github上另一个知名的同类项目redtorch演化而来，感谢redtorch项目作者的启发。  
本人几乎从头到尾90%重构了两次，深感开源不易。  
详细文档请参考wiki  

## 运行环境
建议使用Linux云服务器

## 程序架构
- B/S架构
- northstar项目为服务端
- northstar-monitor项目为web网页监控端
- 交互协议HTTP + websocket
- 数据库为MongoDB
- 前端采用nodejs + vue
- 服务端采用java + springboot

## 启动步骤
假设当前环境是全新的服务器  

下载项目
```
cd ~
git clone https://gitee.com/KevinHuangwl/northstar.git
```

修改配置文件中的邮箱代理配置
```
spring:
  ...

  mail:
    host: smtp.126.com              //不同的邮箱配置不同
    username: ${email}              //为了不上传git，采用启动参数配置
    password: UQNXPDZJIYQTRFRW      //在邮箱网站获取
    default-encoding: UTF-8
    subscribed: ${sub_email}        //为了不上传git，采用启动参数配置
    port: 465
    protocol: smtp
    
    ...
```

准备环境（只需要运行一次）
```
cd ~/northstar
bash env.sh
```

构建程序（每次代码更新后运行）
```
cd ~/northstar
bash build.sh
```

运行程序
```
nohup java -jar -DwsHost=<这里填云服务器内网IP> -Dnsuser=<登陆用户名> -Dnspwd=<登陆密码> -Demail=<代理邮箱名> -Dsub_email=<订阅邮箱名> ～/northstar.jar &
```

如果嫌以上命名过于复杂，可以通过以下方法简化启动命令  
方法一： 把以上命令写成脚本  
方法二： 在.bashrc中加入以上启动参数（对参数的隐藏比方法一要好）  
```
vim ~/.bashrc
```
在文末加入以下设置
```
...
export WSHOST=<这里填云服务器内网IP>
export NSUSER=<登陆用户名>
export NSPWD=<登陆密码>
export EMAIL=<代理邮箱名> 
export SUB_EMAIL=<订阅邮箱名>
```
保存并退出，然后让配置生效
```
source ~/.bashrc
```
再启动程序
```
nohup java -jar ～/northstar.jar &
```


查询日志
```
cd ~/logs/
```

终止程序
```
kill `pgrep java`
```


## 注意事项
- 服务器时间校正
- 本项目为量化爱好者及JAVA开发者搭建，对交易行为并不负责
- 当前项目只能进行手工交易，若要开发量化策略，需要一定的JAVA编程基础
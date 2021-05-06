# my-im

`my-im`是一款基于`netty+kafka+redis+zookeeper`实现的`分布式即时通讯系统`。实现了群聊、私聊、当前在线用户统计等功能，同时可以很方便水平扩展。

## 技术栈

|  技术栈   |         用处         |
| :-------: | :------------------: |
|   netty   |     网络通信框架     |
|   kafka   | 不同服务器间消息转发 |
|   redis   |     统计在线用户     |
| zookeeper | 分布式服务注册与发现 |

## 系统架构
<img src='E:\image-20210506163543932.png' style='float:left; '/>

- `IM` 中的各个组件均采用 `SpringBoot` 构建。
-  采用 `Netty` 构建底层通信。
-  `Redis` 存放在线用户与所连服务器的信息。
-  `Zookeeper` 用于 `im-server` 服务的注册与发现。
-  `Kafka` 用于 不同 `im-server` 之间的消息转发

## TODO LIST

* [x] [群聊]
* [x] [私聊]
* [x] 使用 `Google Protocol Buffer` 高效编解码
* [x] 根据实际情况灵活的水平扩容、缩容
* [x] 服务端自动剔除离线客户端
* [x] 客户端自动重连
* [ ] 聊天记录查询
* [ ] 协议支持消息加密

## 项目模块概览

- **im-client**	——	客户端
- **im-common**	——	公共工具包，主要存放各个中间件工具类
- **im-server**	——	服务端

## 启动

在此之前请确保 zookeeper、redis、kafka运行

先启动im-server，再启动im-client

### 群聊

客户端控制台输入聊天信息即可

### 私聊

首先输入`ls`命令可查看在线用户（暂时需要排除前缀，比如`im_123`，用户id为`123`，而不要输入`im_123`）

然后以为`用户的id:信息`的格式发送消息即可
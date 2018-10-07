# TankWarNet

- Porject

    1. 描述：java 实现的 TankWar 联网版
    
    2. 目的：Server Client 使用 TCP/UDP 进行数据 send & receive
    
    3. 操作：参考 [单机版_README](https://github.com/LearningPracticeTheory/TankWar/blob/master/README.md)

    4. 测试：单机测试 & 局域网内测试

    5. 不足: 异常处理不到位，潜在 BUG    
        
- 测试

    1. 先起 Server 端
    
    2. 单机测试
    \
        多个 Client 端测试 serverIP 可为 localhost or 127.0.0.1
        
    3. 局域网测试
    \
    cmd -> ipconfig -> IPv4 作为 serverIP，udpPort 会随机生成

- jar 运行

    1. cmd -> java -jar NAME.jar（NAME 为 jar 包名称）
    2. jar 打开方式 -> javaw.exe 运行
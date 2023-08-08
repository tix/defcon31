1. 扩展主机环境准备
   a. 确保安装 java 1.8
   b. 确保安装 codedeploy 客户端
   c. 确保配置 相应环境变量DEPLOY_ENV(/etc/profile): --spring.profiles.active=${DEPLOY_ENV}
2. 持续交付“项目”配置文件（gitlab每个项目根目录下）
    build_cd/
    ├── bin
    │   ├── checkhealth.sh #监控检查脚本
    │   ├── start.sh #启动脚本(项目端口号定义)
    │   └── stop.sh #停止脚本
    ├── build.sh #编译脚本，根据不同项目定制
    ├── readMe.txt
    └── S3_Revision #AWS codedeploy-agent 配置脚本，一般不需要改变
        ├── appspec.yml
        └── scripts
            ├── AfterInstall.sh
            ├── ApplicationStart.sh
            ├── ApplicationStop.sh
            ├── DeleteWar.sh
            ├── UpdateApiDoc.sh
            └── ValidateService.sh
3.  持续交付“环境”配置文件（http://gitlab.starpavilion-digital.com/mobilecontent-cloud/cd.git）
    cd/
    ├── {project}
    │   ├── conf.sh #主要定义项目gitlab地址、编译脚本路径、不同环境不同region对应实例
    
4.  暂不考虑主机扩缩容
5.  主机目录规划
    部署路径：/feige/deploy/${PROJECT_NAME}/${component}
    备份路径：/feige/deploy/deployfile/${PROJECT_NAME}/bak/tar
    日志路径：/pdata1/${PROJECT_NAME}  #具体参考log4j定义
    部署日志：/opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log
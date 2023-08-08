#!/bin/bash
# By ivan.wang

echo "...................... get_version() ...................... "
. /opt/codedeploy-agent/deployment-root/${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}/deployment-archive/scripts/Initialize.sh

function user_check(){
    user=www-data
    group=www-data
     
    #create group if not exists
    egrep "^$group" /etc/group >& /dev/null
    if [ $? -ne 0 ];then
        groupadd $group
    fi
     
    #create user if not exists
    egrep "^$user" /etc/passwd >& /dev/null
    if [ $? -ne 0 ];then
        useradd $user -g $group -s /sbin/nologin
    fi
    chown spark:dev /dianyi/log 
    chown www-data:dev /dianyi/deploy
}
function stop_service(){
    echo "PROJECT_NAME=${PROJECT_NAME}"
    echo "COMPONENT=${COMPONENT}"
    echo "DEPLOYMENT_GROUP_ID/DEPLOYMENT_ID=${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}"
    echo "DEPLOY_PATH=${DEPLOY_PATH}"
    echo "DEPLOY_LOG=${DEPLOY_LOG}"
    echo "NOHUP_DEPLOY_LOG=${NOHUP_DEPLOY_LOG}"
    echo "VERSION_FILE_PATH=${VERSION_FILE_PATH}"
    echo "VERSION=${VERSION}"
    echo "CURRENT_VERSION=${CURRENT_VERSION}"
    echo "LAST_VERSION=${LAST_VERSION}"

    echo "...................... stop_service() ...................... "
    
    echo "${DEPLOY_PATH}/${COMPONENT}/build_cd/bin/stop.sh"
    if [ -f ${DEPLOY_PATH}/${COMPONENT}/build_cd/bin/stop.sh ];then
        cd  ${DEPLOY_PATH}/${COMPONENT}
        
        chmod 755 build_cd/bin/*.sh
        ./build_cd/bin/stop.sh
        if [[ $? != 0 ]];then
            echo -e "$(date +%Y-%m-%d" "%H:%M:%S) ${HOST_NAME} Stop Failure." | tee -a ${DEPLOY_LOG}
            exit 1
        else
            echo -e "$(date +%Y-%m-%d" "%H:%M:%S)  ${HOST_NAME} has been stopped successfully." | tee -a ${DEPLOY_LOG}
        fi
        cd -
    else
        echo "First run in host."
    fi
}

function main()
{
    get_variables
    get_version
    #user_check
    create_folders
    stop_service
}
main

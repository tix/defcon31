#!/bin/bash --login
# By ivan.wang

. /opt/codedeploy-agent/deployment-root/${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}/deployment-archive/scripts/Initialize.sh

function start_service()
{
    echo "...................... start_service() ...................... "
    who am i
    
    echo "${DEPLOY_PATH}/${COMPONENT}/build_cd/bin/start.sh"
    cd  ${DEPLOY_PATH}/${COMPONENT}
    
    chmod 755 build_cd/bin/*.sh

    # start the service
    ./build_cd/bin/start.sh aws_access_key=${aws_access_key},aws_secret_access_key=${aws_secret_access_key},NOHUP_DEPLOY_LOG=${NOHUP_DEPLOY_LOG}
    ./build_cd/bin/checkhealth.sh
    if [[ $? != 0 ]];then
        echo -e "$(date +%Y-%m-%d" "%H:%M:%S) ${WAR_NAME} is not deployed on server ${HOST_NAME}! The job will be exited." | tee -a ${DEPLOY_LOG}
        exit 1
    else
        echo -e "$(date +%Y-%m-%d" "%H:%M:%S) ${WAR_NAME} was updated on ${HOST_NAME} successfully." | tee -a ${DEPLOY_LOG}
    fi
    cd -
}

function main()
{
    get_variables
    get_version
    start_service
}
main

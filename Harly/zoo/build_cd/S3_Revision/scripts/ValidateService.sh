#!/bin/bash
# By ivan.wang

. /opt/codedeploy-agent/deployment-root/${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}/deployment-archive/scripts/Initialize.sh

function record_target_version()
{    
    cd  ${DEPLOY_PATH}/${COMPONENT}
    chmod 755 build_cd/bin/*.sh
    ./build_cd/bin/checkhealth.sh true
    if [[ $? != 0 ]];then
        echo -e "$(date +%Y-%m-%d" "%H:%M:%S) ${WAR_NAME} is not deployed on server ${HOST_NAME} for $i times! The job will be exited." | tee -a ${DEPLOY_LOG}
        exit 1
    else
        echo -e "$(date +%Y-%m-%d" "%H:%M:%S) PID is ${COMPONENT_PID},${WAR_NAME} was updated on ${HOST_NAME} successfully." | tee -a ${DEPLOY_LOG}
    fi
    cd -
}

function main()
{
    get_variables
    record_target_version
}

main

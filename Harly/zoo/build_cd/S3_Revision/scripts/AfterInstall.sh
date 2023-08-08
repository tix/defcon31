#!/bin/bash
# By ivan.wang

. /opt/codedeploy-agent/deployment-root/${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}/deployment-archive/scripts/Initialize.sh

function delete_application_folder()
{
    echo "...................... delete_application_folder() ......................"
    cd ${DEPLOY_PATH}
    
    if [[ "${COMPONENT}" != "" ]];then
       rm -rf ${DEPLOY_PATH}/${COMPONENT}/*
       echo "${DEPLOY_PATH}/${COMPONENT} folder is clean up!"
    fi
    
    if [[ ! -d ${COMPONENT} ]];then
        mkdir -p ${COMPONENT}
    fi
    
    # Uncompress
    tar -zxf ./${WAR_NAME} -C ${COMPONENT}
    cd -
}

function main()
{
    get_variables
    delete_application_folder
}

main

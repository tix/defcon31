#!/bin/bash
# By ivan.wang

. /opt/codedeploy-agent/deployment-root/${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}/deployment-archive/scripts/Initialize.sh

function delete_war_files()
{
    echo "...................... delete_war_files() ...................... "
    # Will rename a name based on date & time instead of delete the war file
    cp ${DEPLOY_PATH}/${WAR_NAME}  ${WAR_FILE_BACKUP_PATH}/${WAR_NAME}_${DATE}
    rm -rf ${DEPLOY_PATH}/${WAR_NAME}    
    echo "${DEPLOY_PATH}/${WAR_NAME} file is deleted!"
}

function main()
{
    get_variables
    create_folders
    get_version

    delete_war_files
}

main

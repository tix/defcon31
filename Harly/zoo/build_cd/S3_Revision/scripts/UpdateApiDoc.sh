#!/bin/bash
# By ivan.wang
. /opt/codedeploy-agent/deployment-root/${DEPLOYMENT_GROUP_ID}/${DEPLOYMENT_ID}/deployment-archive/scripts/Initialize.sh
get_variables
echo "${VERSION}" >> ${VERSION_FILE_PATH}
echo "Update Api Doc is not involve ,it will NOT run updateApiDoc.sh."
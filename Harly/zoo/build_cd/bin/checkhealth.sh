#!/bin/bash
#author ivan
source /etc/profile

if [ "${DEPLOY_ENV}" = "fat" ];then
    PORT_STRING=8083
elif [ "${DEPLOY_ENV}" = "prod" ];then
    PORT_STRING=8080
fi
HOST_NAME="127.0.0.1"
VERIFY_STRING="success"
Is_quick_check=$1

#效验成功返回0，失败返回1
function health_check(){
    echo "[health_check] start."
    for ((i=0; i<10; i++))
    do
        echo "FOR:[$i]"
        COMPONENT_PID=`ps -ef| grep "zoo"|grep -v grep|awk '{print $2}'`
        if [[ ${COMPONENT_PID} != "" ]] ; then
            echo "COMPONENT_PID=${COMPONENT_PID}"
            echo "RESPONSE_CODE : curl -L -s -X GET http://${HOST_NAME}:${PORT_STRING}/health/examination"
            RESPONSE_CODE=`curl -L -s -X GET http://${HOST_NAME}:${PORT_STRING}/health/examination | grep "${VERIFY_STRING}"`

            echo "RESPONSE_CODE is: ${RESPONSE_CODE}"
            if [[ ${RESPONSE_CODE} =~ "${VERIFY_STRING}" ]] ; then
                return 0
            fi
        fi

        if [[ $Is_quick_check != "" ]];then
            return 1
        fi
        if [[ $i -lt 9 ]] ; then
            echo -e "PID is ${COMPONENT_PID},zoo is being deployed! [$i] Please wait for a while ..."
        else
            echo -e "zoo is not deployed on server ${HOST_NAME} for $i times! The job will be exited."
            return 1
        fi
        echo "sleep 8"
        sleep 8
    done
}
health_check

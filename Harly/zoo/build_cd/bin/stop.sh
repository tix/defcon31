#!/bin/bash
#author ivan

function stop(){
    #Get PID
    COMPONENT_PID=`ps -ef| grep "zoo"|grep -v grep|awk '{print $2}' `
    echo "mtcenter component COMPONENT_PID ${COMPONENT_PID}"
    if [[ ${COMPONENT_PID} != "" ]] ; then
        
        for ((k=0; k<3; k++))
        do
            #1.暴力杀（二选一）
            kill -9 ${COMPONENT_PID}
            sleep 3
            #2.柔和杀（二选一）
            #自定义逻辑
            
            TMP_PID=`ps -ef| grep "zoo"|grep -v grep|awk '{print $2}' `
            
            if [[ ${COMPONENT_PID} = ${TMP_PID} ]] ; then
                if [[ $k -ge 2 ]] ; then
                    #适合"柔和杀"，杀不死补一刀
                    #kill -9 ${COMPONENT_PID}
                    #sleep 2
                    continue
                fi
            else
                break
            fi
        done
    fi
}
stop
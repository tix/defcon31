#!/bin/bash
shopt -s extglob
COMPONENT=$1
 
function Log(){
    dateFormat=`date "+%Y-%m-%d %H:%M:%S"`
    level="INFO"
    messageShow="message is null"
    if [[ $1 != "" ]];then
        typeset -u level=$1
    fi
    if [[ $2 != "" ]];then
        messageShow=$2
    fi
    printf "[%s] %s %s\n" "$level" "$dateFormat" "$messageShow"
}
function isError(){
  if [ $? -ne 0 ];then
      Log ERROR "ʧ"
    exit 1
  fi
}
function build(){
    Log INFO "Start ${COMPONENT} component build ..."
    Log INFO "[CMD] mvn clean compile  -Dmaven.test.skip=true findbugs:findbugs pmd:pmd"
    mvn clean compile -Dmaven.test.skip=true findbugs:findbugs pmd:pmd
}
build

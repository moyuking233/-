1.如题，无容器内嵌tomcat部署脚本，日志输出到log文件里

```SHELL
#!/bin/bash
## author:陈定恒
## 此脚本用来快速部署springboot项目
source /etc/profile

echo "======开始部署======"



## 获取输入的参数个数，如果为9则直接退出
pcount=$#

if ((pcount==0)); then

echo "需要指定项目名（包含.jar）以及端口号 例如 sh 本脚本名 项目名 端口号";

exit;
fi


## 获取输入的第一个参数

project_name=$1;
port=8080;
if ((pcount>=2)); then
port=$2;
fi

if ! [[ $port == ?(-)+([0-9]) ]]; then
echo "输入的端口号非法"
exit;
fi


echo "输入的项目名为： $project_name";
echo "部署的端口号为：$port (默认为8080)";


fun_deploy(){
  #开始部署
  echo "开始部署项目";
  log_name=${project_name%.*}$port".log";
  nohup java -jar -server -Xms1024m -Xmx1024m -Xmn1024m $project_name --server.port=$port > $log_name 2>&1 &
  echo "项目部署成功 开始查看日志文件，按Ctrl+c退出查看"
  tailf $log_name | while read line;do
      echo $line
  done;
}

pid=`ps -ef | grep $project_name | grep java | grep -v 'grep' | cut -c 9-16`;

if [ ! -n "$pid" ];  then
echo "没有获取到对应项目的pid ,视为项目为第一次部署";
fun_deploy
exit;
fi

echo "获取到的进程号为：pid=$pid";

## 再次检查pid
pid=`ps -ef | grep $project_name | grep java | grep -v 'grep' | cut -c 9-15`;

if [ ! -n "$pid" ];  then
echo "杀死进程成功";
else
echo "杀死进程失败,退出脚本,请手动删除进程 $pid";
exit;
fi

fun_deploy;

```


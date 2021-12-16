# 1.hadoop

## 1.1 用户配置

直接使用root账户配置不太安全，所以新建一个richard的用户

```shell
useradd richard
passwd richard
#修改配置文件赋予richard用户免密sudo权限
vim /etc/sudoers
#进了文件后在轮子组的下面添加关于richard用户的设置
## Allows people in group wheel to run all commands
## %wheel  ALL=(ALL)       ALL
## richard ALL=(ALL)       NOPASSWD:ALL
# 先不急着切换用户 仍旧使用root
```

## 1.2 环境配置

本次配置不适用docker，直接写了个脚本配置，主要是自动化下载和配置jdk（x64）和hadoop的环境变量

```shell
#!/bin/bash
## author:陈定恒
## email:m13411907763@163.com
## note:有任何bug请致信邮箱
## 使用方法，传到你的linux，chmod 777 env.sh
## 然后./env.sh
## 此脚本需要root用户运行
source /etc/profile
echo "安装wget tar"
yum install wget -y
yum install tar -y
yum install iotop -y
echo "卸载原有的open jdk包"
rpm -qa | grep -i java | xargs -n1 rpm -e --nodeps
echo "====创建/opt/module /opt/software两个文件夹 分别存储安装文件 下载文件===="
mkdir /opt /opt/module /opt/software
echo “====开始下载jdk====”
jdk_url="https://code.aliyun.com/kar/oracle-jdk/raw/3c932f02aa11e79dc39e4a68f5b0483ec1d32abe/jdk-8u251-linux-x64.tar.gz"
wget $jdk_url -P /opt/software
echo "====开始解压jdk===="
jdk=${jdk_url##*/}
echo $jdk
tar -zxvf /opt/software/$jdk -C /opt/module/
echo “====解压完毕====”
echo "====配置java环境变量===="
jdk=`find /opt/module -maxdepth 1 -name jdk* -type d`
if [ ! -n "$jdk" ];  then
echo "没有查询到解压路径，请重新执行此脚本"
exit;
fi

file_name="/etc/profile.d/java.sh"
if [ ! -f "$file_name" ];then
rm -rf $file_name
cat>$file_name<<EOF
#JAVA_HOME
export JAVA_HOME=$jdk
export PATH=\$PATH:\$JAVA_HOME/bin
EOF
fi
source /etc/profile
echo "====jdk环境变量配置完成===="
echo "开始配置hadoop"

hadoop_url="https://repo.huaweicloud.com/apache/hadoop/common/hadoop-3.1.3/hadoop-3.1.3.tar.gz"
echo "====开始下载hadoop===="
wget $hadoop_url -P /opt/software
echo "====开始解压hadoop===="
tar -zxvf /opt/software/${hadoop_url##*/} -C /opt/module
echo “====解压完毕====”
echo "====配置hadoop环境变量===="
hadoop=`find /opt/module -maxdepth 1 -name hadoop* -type d`
if [ ! -n "$hadoop" ];  then
echo "没有查询到解压路径，请重新执行此脚本"
exit;
fi

file_name="/etc/profile.d/hadoop.sh"
if [ ! -f "$file_name" ];then
rm -rf $file_name
cat>$file_name<<EOF
#HADOOP_HOME
export HADOOP_HOME=$hadoop 
export PATH=\$PATH:\$HADOOP_HOME/bin
export PATH=\$PATH:\$HADOOP_HOME/sbin
EOF
fi
source /etc/profile
echo "====hadoop环境配置完毕===="
echo "====请重新打开会话窗口 输入hadoop不报错则证明成功===="
echo "====将/opt文件夹所属的用户修改为richard===="
chown richard -R /opt
```

当然，如果每台服务器都要单独vim创建一个这样的文件很麻烦，于是把之前写的分发文件到各个服务器上脚本拿出来，不过在那之前还要配置ssh免密登录。

```shell
## 任意需要免密登录其他服务器的服务器
ssh-keygen -t rsa
##多按几次回车后
cd ~/.ssh
ll
##出现俩文件，私钥id_rsa,公钥id_rsa.pub
## hostname替换为你想免密访问的主机，第一次访问需要输入密码
ssh-copy-id hostname
##配置成功后，通过ssh去访问，如果不需要密码则配置成功，依次对三台主机进行配置
ssh hostname
```

分发脚本

```shell
#!/bin/bash
## author:陈定恒
## email:m13411907763@163.com
## note:有任何bug请致信邮箱
## 使用方法，传到你的linux，chmod 777 xsync.sh
## 然后./xsync.sh 你要复制的文件
## 对于某些需要root用户修改的文件，则必须使用root

#1.获取参数个数，如果没有参数，直接退出
#$# 这个程式的参数个数,if后面记得跟空格
pcount=$#
if ((pcount==0)); then
echo no args;
exit;
fi

#2 获取文件名称
p1=$1
#不是'，而是`
fname=`basename $p1`
echo fname=$fname
#3 获取上级目录到绝对路径 
#-P是绝对路劲选项，防止输入的参数是软链接
pdir=`cd -P $(dirname $p1); pwd`
echo pdir =$pdir

#4 获取当前用户名称
user=`whoami`

#5 循环 将本地数据发送到其他服务器 此处只配置三台机器 机器名自己写，正常来时候集群的名字是同一个前缀配合不同的数字，这边由于情况特殊自己整活了
host=(huaweiyun aliyun moluu)
for i in ${host[@]};
do
echo -------------开始对目标主机${i}传输----------------
rsync -av $pdir/$fname $user@$i:$pdir
done
```

执行完毕后，再切换到richard用户，以下操作使用richard

```shell
su richard
```

## 1.2 hadoop配置

本次配置不考虑本地，伪分布式，直接上集群配置（至少三台主机），核心配置是core-site.xml，hdfs-site.xml，mapred-site.xml，yarn-site.xml这四个文件，这些文件在/opt/module/hadoop-3.1.3/etc/hadoop下都可找到，将以下内容填入<configuration>标签即可

以下是他们作为配置配置文件的总览，至少需要三台以上的主机来组成集群。

NN是NameNode，推荐配置最好的主机来承担，2NN为SecondaryNameNode，一般用来辅助NameNode进行工作

|      | 华为云（huaweiyun）124.70.54.60 | 啊里云（aliyun） 39.102.56.166    | moluu（moluu） 121.37.148.41 |
| ---- | ------------------------------- | --------------------------------- | ---------------------------- |
| HDFS | **NN** + DataNode               | DataNode                          | **2NN** + DataNode           |
| YARN | NodeManager                     | **ResourceManager** + NodeManager | NodeManager                  |

### 1.2.1 core-site.xml

```xml
<configuration>
 <!-- 指定 NameNode 的地址 -->
 <property>
 <name>fs.defaultFS</name>
 <value>hdfs://hadoop102:8020</value>
 </property>
 <!-- 指定 hadoop 数据的存储目录 -->
 <property>
 <name>hadoop.tmp.dir</name>
 <value>/opt/module/hadoop-3.1.3/data</value>
 </property>
 <!-- 配置 HDFS 网页登录使用的静态用户为 atguigu -->
 <property>
 <name>hadoop.http.staticuser.user</name>
 <value>atguigu</value>
 </property>
</configuration>
```

### 1.2.2 hdfs-site.xml  

```xml
<!--	nn 也就是NameNode web端访问地址-->
    <property>
        <name>dfs.namenode.http-address</name>
        <value>huaweiyun:9870</value>
    </property>
<!--    2nn web端访问地址-->
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>moluu:9868</value>
    </property>
```

### 1.2.3 yarn-site.xml

```xml
<!--    指定MR走shuflle-->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
<!--    指定ResourceManager的地址-->
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>aliyun</value>
    </property>
<!--    环境变量的继承-->
    <property>
        <name>yarn.nodemanger.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRER_HOME</value>
    </property>
```

### 1.2.4 mapred-site.xml

```xml
<!--    指定MapReduce程序运行在Yarn上-->
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>

<property>
    <name>yarn.app.mapreduce.am.env</name>
    <value>HADOOP_MAPRED_HOME=/opt/module/hadoop-3.1.3</value>
</property>
<property>
    <name>mapreduce.map.env</name>
    <value>HADOOP_MAPRED_HOME=/opt/module/hadoop-3.1.3</value>
</property>
<property>
    <name>mapreduce.reduce.env</name>
    <value>HADOOP_MAPRED_HOME=/opt/module/hadoop-3.1.3</value>
</property>

```

### 1.2.5 主机名配置

```shell
#虚拟内网才需要修改这个文件
# vim /etc/sysconfig/network-scripts/ifcfg-eth0
#修改启动协议从dhcp动态解析ip改为static静态
# BOOTPROTO="static"
#添加IPADDR，GATEWAY，
# IPADDR=XXX.XXX.XXX.XXX
# GATEWAY=XXX.XXX.XXX.XXX
# DNS1=XXX.XXX.XXX.XXX


#修改内部主机名
vim /etc/hostname
#修改主机和ip之间的映射,要把其他服务器的ip和主机映射一起放进去，其中本机的域名，对于公网服务器搭建的朋友们来说，要写内网ip，不能写公网的ip，否则启动集群的时候无法识别
vim /etc/hosts 
#xxx.xxx.xxx.xxx domain1
#xxx.xxx.xxx.xxx domain2
#xxx.xxx.xxx.xxx domain3

#重启服务器生效
reboot
```

**备注**：应当把各台主机（服务器）之间的防火墙取消掉，对外设置统一防火墙。此处由于都是放在公网的服务器，考虑到安全问题，直接在运营商的外部防火墙设置了ip放行 名单，对于无法设置外部放行ip白名单的服务器，直接开放全部端口，同时做好备份，内部启动软件层面的防火墙，启用软件白名单方面的白名单。

```shell
#查看防火墙状态
service firelld status
#启动防火墙（仅在外部防火墙完全取消的情况下开启，否则内部防火墙要关闭，确保集群之间通信顺畅）
service firelld start
#关闭防火墙（仅在外部防火墙已经放行白名单ip的情况下添加)
service firelld stop
#添加指定需要开放的端口：
firewall-cmd --add-port=443/tcp --permanent
#重载入添加的端口：
firewall-cmd --reload
#查询指定端口是否开启成功：
firewall-cmd --query-port=123/tcp
#放行指定IP
firewall-cmd --permanent --add-rich-rule="rule family="ipv4" source address="121.37.148.41"   accept" 
#重新载入
firewall-cmd --reload
```

### 1.2.6 配置workers文件

修改此文件，然后分发到其他服务器上

```shell
#修改此文件禁止有任何空格，或者多余的换行，每个域名单独一行
vim /opt/module/hadoop-3.1.3/etc/hadoop/workers
huaweiyun
aliyun
moluu
```

## 1.3 启动hadoop

### 1.3.1 格式化hadoop namenode

正常情况下，此命令执行完，没有报错即表示执行完，同时生成data文件夹/opt/module/hadoop-3.1.3/data

```shell
hdfs namenode -format
```

### 1.3.2 启动dfs

```shell
#到huaweiyun执行此命令
start-dfs.sh
```

### 1.3.3 启动yarn

```shell
#到aliyun执行此命令
start-yarn.sh
```

### 1.3.4 查看集群状态

```shell
#查看当今运行的java进程，如果与上方表格一致则证明启动成功
jps
```

## 1.4 测试集群

```shell
#在huaweiyun上执行此命令
#创建文件夹wcinput
hadoop fs -mkdir /wcinput
#上传文件
```


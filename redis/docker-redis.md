## 配置文件在过去同个文件夹下
## vim /opt/docker/redis/conf/redis.conf/redis.conf
## 把配置文件内容丢进去，然后加载
## docker run -p 9526:6379 -v /opt/docker/redis/data:/data -v /opt/docker/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf --name redis -d redis:5.0.12 redis-server /usr/local/etc/redis/redis.conf/redis.conf --appendonly yes

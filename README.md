## 线上商品秒杀项目
为了应对高并发问题，使用了：  
Redis 作为缓存,减少数据库访问：页面，库存信息，User信息，订单信息等...  
RabbitMQ 作为消息队列，Ajax异步请求  
UserUtil.java -- 批量创建User对象并插入数据库，生成 'id+password' .txt文件用以JMeter测试  
seckill.sql -- 数据库文件

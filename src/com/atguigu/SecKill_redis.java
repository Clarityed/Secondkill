package com.atguigu;

import java.io.IOException;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 * 基本实现秒杀过程
 */
public class SecKill_redis {

	public static void main(String[] args) {
		Jedis jedis =new Jedis("192.168.44.168",6379);
		System.out.println(jedis.ping());
		jedis.close();
	}

	//秒杀过程
	public static boolean doSecKill(String uid,String prodId) throws IOException {
		// 1. uid 和 prodId 非空判断
		if (uid == null || prodId == null) {
			return false;
		}
		// 2. 连接 redis
		// Jedis jedis = new Jedis("192.168.254.129", 6379);
		JedisPool jedisPoolInstance = JedisPoolUtil.getJedisPoolInstance();
		Jedis jedis = jedisPoolInstance.getResource();
		// 3. 拼接 key
		// 3.1 库存 key
		String inventoryKey = "SecKill:"+ prodId + ":inventory";
		// 3.2 秒杀成功用户 key
		String userKey = "SecKill:" + prodId + ":user";
		// 实现事务，解决超卖现象所要做的第一步：开启监视 key
		jedis.watch(inventoryKey);
		// 4. 获取库存，如果库存null，秒杀还没有开始
		if (jedis.get(inventoryKey) == null) {
			System.out.println("秒杀活动还没开始！");
			jedis.close();
			return false;
		}
		// 5. 判断用户是否重复秒杀操作
		if (jedis.sismember(userKey, uid)) {
			System.out.println("秒杀成功的用户不能再次秒杀！");
			jedis.close();
			return false;
		}
		// 6. 判断如果商品数量，库存数量小于1，秒杀结束
		if (Integer.parseInt(jedis.get(inventoryKey)) < 1) {
			System.out.println("该商品已经被秒杀完了！");
			jedis.close();
			return false;
		}
		// 7. 秒杀过程
		// 开启事务
		Transaction multi = jedis.multi();
		// 组队阶段操作
		// 7.1 库存 -1
		// jedis.decr(inventoryKey);
		multi.decr(inventoryKey);
		// 7.2 把秒杀成功用户添加清单里面
		// jedis.sadd(userKey, uid);
		multi.sadd(userKey, uid);
		// 执行操作
		List<Object> exec = multi.exec();
		if (exec == null || exec.size() == 0) {
			System.out.println("秒杀失败了！");
			jedis.close();
			return false;
		}
		System.out.println("恭喜用户 uid：" + uid + "秒杀成功！");
		jedis.close();
		return true;
	}
}

















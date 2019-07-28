package com.wuyu.index.easy;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.wuyu.index.easy.EasyIndex.DirectEnum;

import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.Assert;

/**
 * 模块描述：简单索引
 * 
 * @author wuyu
 *
 */
public class EasyIndexTest {
	
	EasyIndex index = new EasyIndex();
	
	//@Before
	@Ignore
	public void init() {
		long s = System.currentTimeMillis();
		for(int i = 0; i< 20000 ; i++) {
			// 初始化数据
			index.createIndex(Arrays.asList(DirectEnum.BEIJING), i);
			index.createIndex(Arrays.asList(DirectEnum.SHANGHAI), i);
			index.createIndex(Arrays.asList(DirectEnum.MOIVE), i);
			index.createIndex(Arrays.asList(DirectEnum.BEIJING), i);
			index.createIndex(Arrays.asList(DirectEnum.MUSIC), i);
		}
//		for(int i = 1000000; i< 10000000 ; i++) {
//			// 初始化数据
//			index.createIndex(Arrays.asList(DirectEnum.BEIJING), i);
//			index.createIndex(Arrays.asList(DirectEnum.MOIVE), i);
//		}
		long e = System.currentTimeMillis();
		System.out.println("create index consume time----->"+(e-s));
	}

	@Test
	@Ignore
	public void test() {
		// 查询定向对应的投放
		long s = System.currentTimeMillis();
		List<Integer> resultList = index.query(Arrays.asList(DirectEnum.BEIJING,DirectEnum.MOIVE,DirectEnum.MUSIC));
		long e = System.currentTimeMillis();
		System.out.println("query consume time----->"+(e-s));
//		resultList.forEach(item -> {
//			System.out.println(item);
//		});
	}
	
	@Test
	public void testAddIndex() {
		index.addIndex(DirectEnum.BEIJING, 1);
		List<Integer> castIds = index.query(Arrays.asList(DirectEnum.SHANGHAI));
		Assert.assertTrue(castIds.size() == 0);
		index.addIndex(DirectEnum.SHANGHAI, 2);
		castIds = index.query(Arrays.asList(DirectEnum.SHANGHAI));
		Assert.assertTrue(castIds.contains(2));
		Assert.assertFalse(castIds.contains(1));
		index.addIndex(DirectEnum.MUSIC, 1);
		castIds = index.query(Arrays.asList(DirectEnum.BEIJING,DirectEnum.MUSIC));
		Assert.assertTrue(castIds.contains(1));
	}

}

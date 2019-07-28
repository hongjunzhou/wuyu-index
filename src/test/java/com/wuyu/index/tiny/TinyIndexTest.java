package com.wuyu.index.tiny;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;


public class TinyIndexTest {
	
	@Test
	public void test1() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 1));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		List<String> conditionList = new ArrayList<String>();
		conditionList.add("31");
		conditionMap.put("area", conditionList);
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionList.add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1);
		
		conditionList.add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
	}
	
	@Test
	public void test2() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 1));
		targetingList.add(new Targeting<Integer>(1, "area", "31", 1));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		List<String> conditionList = new ArrayList<String>();
		conditionList.add("10");
		conditionMap.put("area", conditionList);
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionList.add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionList.add("12");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionList.add("31");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionList = new ArrayList<String>();
		conditionList.add("31");
		conditionMap.put("area", conditionList);
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
	}
	
	@Test
	public void test3() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 1));
		targetingList.add(new Targeting<Integer>(1, "video", "a", 1));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		List<String> conditionList = new ArrayList<String>();
		conditionList.add("10");
		conditionMap.put("area", conditionList);
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionList.add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionList = new ArrayList<String>();
		conditionList.add("x");
		conditionMap.put("video", conditionList);
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionList.add("a");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
	}
	
	@Test
	public void test4() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 1));
		targetingList.add(new Targeting<Integer>(1, "video", "a", 1));
		targetingList.add(new Targeting<Integer>(2, "area", "31", 1));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("area", new ArrayList<String>());
		
		conditionMap.get("area").add("10");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("area").add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("video", new ArrayList<String>());
		conditionMap.get("video").add("x");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("video").add("a");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		conditionMap.get("area").add("31");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 2 && result.contains(1) && result.contains(2));
	}
	
	
	@Test
	public void test5() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 1));
		targetingList.add(new Targeting<Integer>(1, "video", "a", 1));
		targetingList.add(new Targeting<Integer>(2, "area", "11", 1));
		targetingList.add(new Targeting<Integer>(2, "crowd", "abc", 1));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("area", new ArrayList<String>());
		
		conditionMap.get("area").add("10");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("area").add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("video", new ArrayList<String>());
		conditionMap.get("video").add("x");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("video").add("a");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.put("crowd", new ArrayList<String>());
		conditionMap.get("crowd").add("xxx");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.get("crowd").add("abc");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 2 && result.contains(1) && result.contains(2));
	}
	
	@Test
	public void test6() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 0));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.put("area", new ArrayList<String>());
		conditionMap.get("area").add("31");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.get("area").add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
	}
	
	
	@Test
	public void test7() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(1, "area", "11", 0));
		targetingList.add(new Targeting<Integer>(1, "area", "31", 0));
		targetingList.add(new Targeting<Integer>(1, "video", "a", 0));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.put("area", new ArrayList<String>());
		conditionMap.get("area").add("13");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.get("area").add("12");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.get("area").add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("area").remove("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.put("video", new ArrayList<String>());
		conditionMap.get("video").add("xxx");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(1));
		
		conditionMap.get("video").add("a");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
	}
	
	
	@Test
	public void test8() {
		List<Targeting<Integer>> targetingList = new ArrayList<Targeting<Integer>>();
		targetingList.add(new Targeting<Integer>(105, "area", "11", 1));
		targetingList.add(new Targeting<Integer>(105, "area", "31", 1));
		targetingList.add(new Targeting<Integer>(105, "area", "440100", 1));
		targetingList.add(new Targeting<Integer>(105, "video", "d", 0));
		targetingList.add(new Targeting<Integer>(105, "position", "weibo.com", 1));
		targetingList.add(new Targeting<Integer>(105, "position", "t.sina.com.cn", 1));
		TinyIndex<Integer> index = new TinyIndex<Integer>(targetingList);
		
		Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();
		
		Set<Integer> result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("area", new ArrayList<String>());
		conditionMap.get("area").add("13");
		conditionMap.get("area").add("11");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("video", new ArrayList<String>());
		conditionMap.get("video").add("xxx");
		conditionMap.get("video").add("a");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.put("position", new ArrayList<String>());
		conditionMap.get("position").add("www.weibo.com");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("position").add("weibo.com");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(105));
		
		conditionMap.get("video").add("d");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("position").remove("weibo.com");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("position").add("t.sina.com.cn");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 0);
		
		conditionMap.get("video").remove("d");
		result = index.search(conditionMap);
		Assert.assertTrue(result.size() == 1 && result.contains(105));
	}
}

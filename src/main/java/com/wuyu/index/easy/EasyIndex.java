package com.wuyu.index.easy;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块描述：简单版索引
 * 
 * @author wuyu
 *
 */
public class EasyIndex {
	
	//每个定向的投放位图
	private Map<DirectEnum, BitSet> map = new HashMap<DirectEnum, BitSet>();

	/**
	 * 功能描述：查询定向对应的投放
	 *
	 * @param directs
	 *            定向集合信息
	 * @return 返回适配的投放ID
	 */
	public List<Integer> query(List<DirectEnum> directs) {
		List<Integer> resultList = new ArrayList<Integer>();

		if (directs.isEmpty()) {
			return resultList;
		}
		BitSet castBitSet = null;
		// 遍历定向
		for (DirectEnum direct : directs) {
			BitSet directBitSet = map.get(direct);
			// 没有这个定向说明一个适配的投放都没有
			if (directBitSet == null) {
				break;
			}else {
				//克隆一个相同的位图
				castBitSet = (BitSet)directBitSet.clone();
			}
			castBitSet.and(directBitSet);
		}
		
		//没有对应的定向数据
		if(castBitSet == null) {
			return resultList;
		}
		
		// 遍历适配的信息(寻找下标为1的位图)
		for (int i = castBitSet.nextSetBit(0); i >= 0; i = castBitSet.nextSetBit(i + 1)) {
			resultList.add(i);
		}
		return resultList;
	}

	/**
	 * 功能描述：创建索引
	 *
	 * @param directs
	 *            定向信息
	 * @param castId
	 *            投放ID
	 */
	public void createIndex(List<DirectEnum> directs, Integer castId) {
		for (DirectEnum direct : directs) {
			BitSet bitSet = map.get(direct);
			if (bitSet == null) {
				bitSet = new BitSet();
				map.put(direct, bitSet);
			}
			bitSet.set(castId, true);
		}
	}
	
	/**
	 * 功能描述：增加索引
	 * @param direct
	 * @param castId
	 */
	public void addIndex(DirectEnum direct,Integer castId) {
		BitSet bitSet = map.get(direct);
		if(bitSet == null) {
			bitSet = new BitSet();
			map.put(direct, bitSet);
		}
		bitSet.set(castId, true);
	}

	/**
	 * 功能描述：删除索引
	 *
	 * @param castId
	 *            投放ID
	 */
	public void deleteIndex(Integer castId) {
		for (BitSet bitSet : map.values()) {
			bitSet.clear(castId);
		}
	}

	/**
	 * 模块描述：定向枚举
	 */
	public static enum DirectEnum {
		BEIJING, SHANGHAI, MOIVE, MUSIC
	}
}

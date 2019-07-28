package com.wuyu.index.tiny;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 模块描述：tiny 索引
 * 
 * @author wuyu
 *
 * @param <T>
 */
public class TinyIndex<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 正定向 */
	public static final int POSITIVE = 1;
	/** 反定向 */
	public static final int negative = 0;
	/**
	 * 定向类型
	 */
	private static final String[] TARGETING = {"video","area","position","crowd"};
	
	/**
	 * 普通索引模型
	 */
	private BitSetIndexModel bitSetIndexModel;
	/**
	 * 区间索引模型
	 */
	private Map<String, List<TargetingRange>> rangeIndexModel;

	public TinyIndex() {
		this.bitSetIndexModel = new BitSetIndex().createIndex();
	}
	
	public TinyIndex(List<Targeting<T>> targetingList) {
		this.init(targetingList);
	}

	public TinyIndex(List<Targeting<T>> targetingList, Map<String, List<TargetingRange>> rangeIndexModel) {
		this.init(targetingList);
		this.rangeIndexModel = rangeIndexModel;
	}

	/**
	 * 功能描述：初始化定向位图索引模型
	 * 
	 * @param directionList
	 */
	private void init(List<Targeting<T>> directionList) {
		BitSetIndex index = new BitSetIndex();
		for (Targeting<T> d : directionList) {
			index.constructIndexData(d.getType().trim(), d.getValue().trim(), d.getId(), d.getIsPositive());
		}
		this.bitSetIndexModel = index.createIndex();
	}

	/**
	 * 功能描述：查询定向对应的投放信息
	 * 
	 * @param conditionMap
	 * @return
	 */
	public Set<Integer> search(Map<String, List<String>> conditionMap) {
		// 处理区间类型的倒排索引
		replaceRangeValue(conditionMap);
		Map<String, String[]> conditions = new HashMap<String, String[]>();
		for (String key : conditionMap.keySet()) {
			List<String> conditionList = conditionMap.get(key);
			conditions.put(key, conditionList.toArray(new String[conditionList.size()]));
		}
		List<Object> bitSetSearcherResult = IndexSearcher.doQuery(conditions, this.bitSetIndexModel,TARGETING);
		Set<Integer> result = new HashSet<Integer>();
		for (Object id : bitSetSearcherResult) {
			result.add((Integer) id);
		}
		return result;
	}

	/**
	 * 功能描述：区间定向转换
	 * 
	 * @param coniditionMap
	 */
	private void replaceRangeValue(Map<String, List<String>> coniditionMap) {
		if (rangeIndexModel == null || rangeIndexModel.size() == 0) {
			return;
		}
		for (String rangeKey : rangeIndexModel.keySet()) {
			List<TargetingRange> rangeList = rangeIndexModel.get(rangeKey);
			List<String> conditionValueList = coniditionMap.get(rangeKey);
			if (conditionValueList == null) {
				continue;
			}
			Set<String> ids = new HashSet<String>();
			for (String value : conditionValueList) {
				try {
					double v = Double.parseDouble(value);
					for (TargetingRange range : rangeList) {
						if ((range.getMin() == null || v >= range.getMin())
								&& (range.getMax() == null || v <= range.getMax())) {
							ids.add(range.getId());
						}
					}
				} catch (Exception e) {
				}
			}
			coniditionMap.put(rangeKey, new ArrayList<String>(ids));
		}
	}
}

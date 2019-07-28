package com.wuyu.index.tiny;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * 模块描述：索引检索
 * 
 * @author wuyu
 *
 */
public class IndexSearcher {

	/**
	 * 功能描述：按定向查询投放ID
	 * 
	 * @param conditionMap
	 * @param indexModel
	 * @param allDirections
	 * @return
	 */
	public static List<Object> doQuery(Map<String, String[]> conditionMap, BitSetIndexModel indexModel,
			String[] allDirections) {
		// 预处理定向条件
		preDealConditionMap(conditionMap, allDirections);
		// 正定向处理---------------start
		// 取正定向结果
		Map<String, Map<String, BitSet>> positive = indexModel.getIndexModel().get("+");
		int size = indexModel.getCastIds().size();
		BitSet poi = null;
		if (positive != null) {
			poi = getPositiveResult(conditionMap, positive);
		}
		if (poi == null) {// 正定向可能为空
			poi = new BitSet(size);
		}
		// 正定向处理---------------end

		// 取反定向结果---------------start
		Map<String, Map<String, BitSet>> dis_inx = indexModel.getIndexModel().get("-");
		// 与正定向的差异 1、不需要处理通投 2、维度之前是与的关系，满足其中一个反定向都不能再投
		if (dis_inx != null) {
			BitSet dis = getNegativeResult(conditionMap, dis_inx);
			if (dis != null && dis.cardinality() > 0) {
				dis.flip(0, size);
				poi.and(dis);
			}
		}
		// 投放数量
		int resultCount = poi.cardinality();
		List<Object> resultCastIds = new ArrayList<Object>(resultCount);
		int i = 0;
		while (resultCount-- > 0) {
			int index = poi.nextSetBit(i);
			resultCastIds.add(indexModel.getCastIds().get(index));
			i = index + 1;
		}
		// 取反定向结果---------------end

		return resultCastIds;
	}

	/**
	 * 功能描述：获取反定向投放位图
	 * 
	 * @param conditionMap
	 * @param descIndex
	 * @return
	 */
	private static BitSet getNegativeResult(Map<String, String[]> conditionMap,
			Map<String, Map<String, BitSet>> descIndex) {
		List<BitSet> bs = new ArrayList<BitSet>();
		for (Map.Entry<String, String[]> entry : conditionMap.entrySet()) {
			String direct_type = entry.getKey();
			String[] direct_value = entry.getValue();

			Map<String, BitSet> map = descIndex.get(direct_type);
			if (map == null) {// 数据库定向条件中无此维度的，可以跳过
				continue;
			}
			BitSet b = getTargetingBitSet(map, direct_value);
			if (b == null) {// 对于反定向，找不到一个维度则继续找其他维度
				continue;
			} else {
				bs.add(b);
			}
		}
		if (bs.isEmpty()) {
			return null;
		}
		BitSet res_o = bs.get(0);
		BitSet res = (BitSet) res_o.clone();
		for (BitSet b : bs) {
			res.or(b);// 反定向，任一条件满足反定向，都不能投
		}
		return res;
	}

	/**
	 * 功能描述：获取正定向投放位图
	 * 
	 * @param conditionMap
	 * @param descIndex
	 * @return
	 */
	private static BitSet getPositiveResult(Map<String, String[]> conditionMap,
			Map<String, Map<String, BitSet>> descIndex) {
		List<BitSet> bs = new ArrayList<BitSet>();
		for (Map.Entry<String, String[]> entry : conditionMap.entrySet()) {
			String direct_type = entry.getKey();
			String[] direct_value = entry.getValue();
			Map<String, BitSet> map = descIndex.get(direct_type);
			if (map == null) {// 数据库定向条件中无此维度的，可以跳过
				continue;
			}
			BitSet b = getTargetingBitSet(map, direct_value);
			if (b == null) {// 如果在当前（数据库的）定向维度值中找不到投放，那么直接返回找不到~
				return null;
			} else {
				bs.add(b);
			}
		}
		if (bs.isEmpty()) {
			return null;
		}
		BitSet res_o = bs.get(0);
		BitSet res = (BitSet) res_o.clone();
		for (BitSet b : bs) {
			res.and(b);
		}
		return res;
	}

	/**
	 * 功能描述：预处理定向条件
	 * 
	 * @param conditionMap
	 * @param allTargeting
	 */
	private static void preDealConditionMap(Map<String, String[]> conditionMap, String[] allTargeting) {
		if (allTargeting == null || allTargeting.length == 0) {
			return;
		}
		for (String d : allTargeting) {
			if (conditionMap.get(d) == null || conditionMap.get(d).length == 0) {
				conditionMap.put(d, new String[] { "other" });
			}
		}
	}

	/**
	 * 功能描述：获取多维度，投放位图
	 * 
	 * @param direct_value
	 * @param map
	 * @return
	 */
	private static BitSet getTargetingBitSet(Map<String, BitSet> map, String[] direct_value) {
		BitSet b = null;
		if (direct_value.length == 1) {
			b = getTargetingBitSet(map, direct_value[0]);
		} else if (direct_value.length > 1) {
			BitSet bo = getTargetingBitSet(map, direct_value[0]);
			// 非线程同步，这里需要clone出一个对象
			b = (bo == null ? null : (BitSet) bo.clone());
			for (int i = 1; i < direct_value.length; i++) {
				BitSet bi = getTargetingBitSet(map, direct_value[i]);
				if (bi != null) {
					if (b == null) {
						b = (BitSet) bi.clone();
					} else {
						b.or(bi);
					}
				}
			}
		}
		return b;
	}

	/**
	 * 功能描述：获取单维度，投放位图
	 * 
	 * @param map
	 *            Map<定向值, 投放位图>
	 * @param key
	 *            定向类型
	 * 
	 * @return 投放位图
	 */
	private static BitSet getTargetingBitSet(Map<String, BitSet> map, String key) {
		BitSet b = map.get(key);
		if (b == null) {
			b = map.get("other");
		}
		return b;
	}
}

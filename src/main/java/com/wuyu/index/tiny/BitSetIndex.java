package com.wuyu.index.tiny;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 模块描述：bitset索引
 * 
 * @author wuyu
 *
 */
public class BitSetIndex {
	/**
	 * 投放集合
	 */
	private Set<Object> castSets = new HashSet<Object>();

	/**
	 * 倒排索引 Map<正反定向(+or-),Map<定向类型,Map<定向值,List<投放ID>>>>
	 */
	private Map<String, Map<String, Map<String, List<Object>>>> allTargetingIndex = new HashMap<String, Map<String, Map<String, List<Object>>>>();

	/**
	 * 正排索引 Map<正反定向(+or-),Map<投放ID,Set<定向类型>>>
	 */
	private Map<String, Map<Object, Set<String>>> allCastTargetingIndex = new HashMap<String, Map<Object, Set<String>>>();

	/**
	 * 功能描述：构建索引数据
	 * 
	 * @param targetingType
	 *            定向类型
	 * @param targetingValue
	 *            定向值(用逗号分隔)
	 * @param castId
	 *            投放ID
	 * @param positive
	 *            正反定向 1:正,0:反
	 */
	public void constructIndexData(String targetingType, String targetingValue, Object castId, int positive) {

		String[] targetingArr = targetingValue.split(",");
		String isPositive = positive == 0 ? "-" : "+";

		// 构建倒排数据结构---------------------------start
		// 倒排索引(定向索引) Map<定向类型,Map<定向值,List<投放ID>>>
		Map<String, Map<String, List<Object>>> targetingIndex = allTargetingIndex.get(isPositive);
		if (targetingIndex == null) {
			targetingIndex = new HashMap<String, Map<String, List<Object>>>();
			allTargetingIndex.put(isPositive, targetingIndex);
		}
		// 定向对应的投放集合
		Map<String, List<Object>> targetingTypeCast = targetingIndex.get(targetingType);
		if (targetingTypeCast == null) {
			targetingTypeCast = new HashMap<String, List<Object>>();
			targetingIndex.put(targetingType, targetingTypeCast);
		}
		// 遍历定向，向定向中添加投放ID
		for (String key : targetingArr) {
			List<Object> ids = targetingTypeCast.get(key);
			if (ids == null) {
				ids = new ArrayList<Object>();
				targetingTypeCast.put(key, ids);
			}
			ids.add(castId);
		}
		// 构建倒排数据结构---------------------------end

		// 构建正排数据结构---------------------------start
		Map<Object, Set<String>> castTargetingIndex = allCastTargetingIndex.get(isPositive);
		if (castTargetingIndex == null) {
			castTargetingIndex = new HashMap<Object, Set<String>>();
			allCastTargetingIndex.put(isPositive, castTargetingIndex);
		}
		// 向投放中添加所有定向信息
		Set<String> set = castTargetingIndex.get(castId);
		if (set == null) {
			set = new HashSet<String>();
			castTargetingIndex.put(castId, set);
		}
		set.add(targetingType);
		castSets.add(castId);
		// 构建正排数据结构---------------------------end
	}

	/**
	 * 功能描述：构建索引位图
	 */
	public BitSetIndexModel createIndex() {
		List<Object> castIds;
		// 索引模型 Map<正反定向(+or-),Map<定向类型,Map<定向值，定向值对应的投放位图>>>
		Map<String, Map<String, Map<String, BitSet>>> index;
		if (allTargetingIndex == null && allTargetingIndex.size() < 1) {
			castIds = new ArrayList<Object>(0);
			index = new HashMap<String, Map<String, Map<String, BitSet>>>();
			return new BitSetIndexModel(index, castIds);
		}
		// 初始化模型数据
		castIds = new ArrayList<Object>(castSets);
		index = new HashMap<String, Map<String, Map<String, BitSet>>>();
		// 正定向
		// Map<定向类型,Map<定向值,List<投放ID>>
		Map<String, Map<String, List<Object>>> posTargetingMap = allTargetingIndex.get("+");
		// Map<投放ID,Set<定向类型>
		Map<Object, Set<String>> posCastTargetingtMap = allCastTargetingIndex.get("+");
		// Map<定向类型,Map<定向值，定向值对应的投放位图>>
		Map<String, Map<String, BitSet>> positiveMap = makeIndex(posTargetingMap, posCastTargetingtMap, castIds, 1);
		// 反定向
		Map<String, Map<String, List<Object>>> negTargetingMap = allTargetingIndex.get("-");
		Map<Object, Set<String>> negCastTargetingMap = allCastTargetingIndex.get("-");
		Map<String, Map<String, BitSet>> dispositiveMap = makeIndex(negTargetingMap, negCastTargetingMap, castIds, 0);
		// 处理正定向通投
		positiveMap = dealGeneralTargeting(positiveMap, posTargetingMap, negTargetingMap, posCastTargetingtMap,
				castIds);
		index.put("+", positiveMap);
		index.put("-", dispositiveMap);
		return new BitSetIndexModel(index, castIds);
	}

	/**
	 * 功能描述：通投处理
	 * 
	 * @param positiveMap
	 * @param posTargetingMap
	 * @param negTargetingMap
	 * @param posCastTargetingtMap
	 * @param castIds
	 * @return
	 */
	private Map<String, Map<String, BitSet>> dealGeneralTargeting(Map<String, Map<String, BitSet>> positiveMap,
			Map<String, Map<String, List<Object>>> posTargetingMap,
			Map<String, Map<String, List<Object>>> negTargetingMap, Map<Object, Set<String>> posCastTargetingtMap,
			List<Object> castIds) {
		if (castIds == null || castIds.isEmpty()) {
			return positiveMap;
		}
		// castId和下标的对应关系Map
		int size = castIds.size();

		if (positiveMap != null && !positiveMap.isEmpty() && posTargetingMap != null && !posTargetingMap.isEmpty()) {
			for (Map.Entry<String, Map<String, List<Object>>> m : posTargetingMap.entrySet()) {// 正定向可以直接遍历
				// 处理常规定向
				String key = m.getKey(); // 定向维度
				Map<String, BitSet> rs = positiveMap.get(key);
				// 处理当前这个维度通投的情况
				BitSet other = new BitSet(size);
				boolean hasOther = false;
				for (int i = 0; i < size; i++) {
					Object castId = castIds.get(i);
					Set<String> ds = posCastTargetingtMap.get(castId);
					if (ds == null || !ds.contains(key)) {
						// 如果一个投放在当前维度上没有定向，那么其下所有value对应的bitset都置位。并且要加上other定向
						for (Map.Entry<String, BitSet> entry : rs.entrySet()) {
							entry.getValue().set(i);
						}
						hasOther = true;
						other.set(i);
					}
				}
				if (hasOther) {
					rs.put("other", other);
				}
			}
		}

		if (negTargetingMap != null && !negTargetingMap.isEmpty()) {
			if (positiveMap == null) {// 只有反定向时positiveMap可能为空
				positiveMap = new HashMap<String, Map<String, BitSet>>();
			}
			for (Map.Entry<String, Map<String, List<Object>>> m : negTargetingMap.entrySet()) {
				String key = m.getKey(); // 反定向维度

				Map<String, BitSet> rs = positiveMap.get(key);
				if (rs != null) {
					continue;
				}
				rs = new HashMap<String, BitSet>();

				BitSet other = new BitSet(size);
				other.flip(0, size);
				rs.put("other", other);
				positiveMap.put(key, rs);
			}
		}
		return positiveMap;

	}

	/**
	 * 功能描述：创建索引
	 * 
	 * @param targetingMap
	 *            Map<定向类型, Map<定向值, List<投放ID>>>
	 * @param castTargetingMap
	 *            Map<投放ID, Set<定向类型>>
	 * @param castIds
	 *            投放ID
	 * @param isPositive
	 *            正反定向
	 * @return Map<定向类型,Map<定向值，定向值对应的投放位图>>
	 */
	private Map<String, Map<String, BitSet>> makeIndex(Map<String, Map<String, List<Object>>> targetingMap,
			Map<Object, Set<String>> castTargetingMap, List<Object> castIds, int isPositive) {
		if (targetingMap == null) {
			return null;
		}
		int size = castIds.size();
		// castId，集合映射
		Map<Object, Integer> castIndexMap = new HashMap<Object, Integer>(size);
		for (int i = 0; i < size; i++) {
			castIndexMap.put(castIds.get(i), i);
		}

		// Map<定向类型,Map<定向值，定向值对应的投放位图>>
		Map<String, Map<String, BitSet>> result = new HashMap<String, Map<String, BitSet>>();
		for (Map.Entry<String, Map<String, List<Object>>> m : targetingMap.entrySet()) {
			// 处理常规定向
			String key = m.getKey(); // 定向维度
			Map<String, List<Object>> values = m.getValue();
			Map<String, BitSet> rs = result.get(key);
			if (rs == null) {
				rs = new HashMap<String, BitSet>();
				result.put(key, rs);
			}
			for (Entry<String, List<Object>> dc : values.entrySet()) {
				String k = dc.getKey(); // 定向维度值
				List<Object> value = dc.getValue(); // 投放单ID列表
				BitSet b = rs.get(k);
				if (b == null) {
					b = new BitSet(size);
					rs.put(k, b);
				}
				for (Object cid : value) {
					b.set(castIndexMap.get(cid));
				}
			}
		}
		return result;
	}
}

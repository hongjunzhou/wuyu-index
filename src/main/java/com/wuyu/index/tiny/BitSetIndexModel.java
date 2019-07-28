package com.wuyu.index.tiny;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * 模块描述：bitset 索引模型
 * 
 * @author wuyu
 *
 */
public class BitSetIndexModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 索引模型 Map<正反定向(+or-),Map<定向类型,Map<定向值，定向值对应的投放位图>>>
	 */
	private Map<String, Map<String, Map<String, BitSet>>> indexModel;

	/**
	 * 投放集合
	 */
	private List<Object> castIds;

	public BitSetIndexModel(Map<String, Map<String, Map<String, BitSet>>> indexModel, List<Object> castIds) {
		this.indexModel = indexModel;
		this.castIds = castIds;
	}

	public Map<String, Map<String, Map<String, BitSet>>> getIndexModel() {
		return indexModel;
	}

	public List<Object> getCastIds() {
		return castIds;
	}

}

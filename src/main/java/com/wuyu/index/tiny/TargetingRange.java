package com.wuyu.index.tiny;

import java.io.Serializable;

/**
 * 模块描述：定向区间
 * 
 * @author wuyu
 *
 */
public class TargetingRange implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	/**
	 * 最大值
	 */
	private Double min;
	/**
	 * 最小值
	 */
	private Double max;
	
	
	public TargetingRange(Double min, Double max) {
		this.min = min;
		this.max = max;
		this.id = "" + min + "-" + max;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public String getId() {
		return id;
	}
	
}

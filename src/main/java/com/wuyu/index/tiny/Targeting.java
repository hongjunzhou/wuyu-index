package com.wuyu.index.tiny;

import java.io.Serializable;

/**
 * 模块描述：广告定向实体
 * 
 * @author wuyu
 *
 */
public class Targeting<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 投放ID
	 */
	private T id;
	/**
	 * 定向类型
	 */
	private String type;
	/**
	 * 定向值
	 */
	private String value;
	/**
	 * 是否正定向 1:正定向,0:反定向
	 */
	private int isPositive;

	public Targeting(T id, String type, String value, int isPositive) {
		this.id = id;
		this.type = type;
		this.value = value;
		this.isPositive = isPositive;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIsPositive() {
		return isPositive;
	}

	public void setIsPositive(int isPositive) {
		this.isPositive = isPositive;
	}

	@Override
	public String toString() {
		return "Targeting [id=" + id + ", type=" + type + ", value=" + value + ", isPositive=" + isPositive + "]";
	}

}

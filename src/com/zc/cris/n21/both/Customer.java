package com.zc.cris.n21.both;

import java.util.HashSet;
import java.util.Set;

public class Customer {
	
	private Integer id;
	private String name;

	
	//1. 声明集合类型的时候，需要使用接口类型（Set），因为hibernate在获取
	//n的一方数据时，返回的是hibernate内置的集合类型，而不是javase的一个
	//标准实现
	//2. 需要把集合进行初始化，否则保存数据的时候会报空指针异常
	private Set<Order> orders = new HashSet<>();
			
	
	public Set<Order> getOrders() {
		return orders;
	}
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

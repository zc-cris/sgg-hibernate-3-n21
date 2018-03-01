package com.zc.cris.n21;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class TestHibernate2 {
	
	private SessionFactory sessionFactory = null;
	private Session session = null;
	private Transaction transaction = null;

	
	@Test
	void testMany21Delete() {
		Customer customer = this.session.get(Customer.class, 2);
		
//		<!-- 特别注意hibernate 所使用的数据库方言需要进行如下设置 -->
//    	<property name="dialect">org.hibernate.dialect.MySQL5InnoDBDialect</property>
		
//		<!-- hibernate 所使用的数据库方言如下，将不会创建外键，因为不能识别部分命令 -->
//    	<property name="dialect">org.hibernate.dialect.MySQL5Dialect</property>
		
		//在不设置级联关系的时候，如果一还有多的引用，那么一就无法被删除
		this.session.delete(customer);
		
	}
	
	@Test
	void testMany21Get() {
		
		//查询多默认会延迟加载一，只有使用到了一才会发送select语句进行查询一，所以获取order对象的时候，其关联的
		//customer对象是一个代理对象
		Order order = this.session.get(Order.class, 3);
		System.out.println(order.getName());
		
		//查询一的时候，如果session已经关了就会产生懒加载异常
		this.session.close();
		
		System.err.println(order.getCustomer().getName());
	}
	
	@Test
	void testMany21Save() {
		Customer customer = new Customer();
		customer.setName("james");
		
		Order order1 = new Order();
		order1.setName("order1");
		Order order2 = new Order();
		order2.setName("order2");
		
		//设定关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//需要先保存一，再保存多（效率更高，否则会多出update语句，因为先插入多会无法确定外键值）
		this.session.save(customer);
		this.session.save(order1);
		this.session.save(order2);
	}
	
	
	/**
	 * 
	 * @MethodName: init
	 * @Description: TODO (执行每次@Test 方法前需要执行的方法)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	@BeforeEach		
	public void init() {
		
		 //在5.1.0版本汇总，hibernate则采用如下新方式获取：
	    //1. 配置类型安全的准服务注册类，这是当前应用的单例对象，不作修改，所以声明为final
	    //在configure("cfg/hibernate.cfg.xml")方法中，如果不指定资源路径，默认在类路径下寻找名为hibernate.cfg.xml的文件
	    final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("/hibernate.cfg.xml").build();
	    //2. 根据服务注册类创建一个元数据资源集，同时构建元数据并生成该应用唯一（一般情况下）的一个session工厂
	    this.sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
	    this.session = this.sessionFactory.openSession();
	    this.transaction = this.session.beginTransaction();
		
	}
	
	/**
	 * 
	 * @MethodName: destroy
	 * @Description: TODO (执行每次@Test 方法后需要执行的方法注解)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	@AfterEach		
	public void destroy() {
		
		this.transaction.commit();
		this.session.close();
		this.sessionFactory.close();
	}
	


}

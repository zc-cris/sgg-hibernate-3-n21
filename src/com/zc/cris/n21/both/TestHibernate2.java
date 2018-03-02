package com.zc.cris.n21.both;

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
	void testCascadeDelete() {
		Customer customer = this.session.get(Customer.class, 1);
		//孤儿删除，只会删除多，不会删除一
		customer.getOrders().clear();
	}
	
	
	
	@Test
	void testMany21DeleteBoth() {
		Customer customer = this.session.get(Customer.class, 2);
		
//		<!-- 特别注意hibernate 所使用的数据库方言需要进行如下设置 -->
//    	<property name="dialect">org.hibernate.dialect.MySQL5InnoDBDialect</property>
		
//		<!-- hibernate 所使用的数据库方言如下，将不会创建外键，因为不能识别部分命令 -->
//    	<property name="dialect">org.hibernate.dialect.MySQL5Dialect</property>
		
		//在不设置级联关系的时候，如果一还有多的引用，那么一就无法被删除
		this.session.delete(customer);
		
	}
	
	@Test
	void testMany21UpdateBoth() {
		Customer customer = this.session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setName("jjj");
	}
	
	@Test
	void testMany21GetBoth() {
		
		//1. 查询一的时候对n的一端使用延迟加载
		Customer customer = this.session.get(Customer.class, 3);
		System.out.println(customer.getName());
		
		//查询一的时候，如果session已经关了就可能产生懒加载异常（后面如果还要查询多的具体数据就会发生）
		//this.session.close();
		
		//2.返回多的一端的时候，是使用Hibernate的内置集合类型
		//org.hibernate.collection.internal.PersistentSet
		//这是一个javase的Set接口的实现类，具有延迟加载和存放代理对象的功能
		//在需要使用集合中的元素的时候才会进行真正的初始化
		System.err.println(customer.getOrders().getClass().getName());
		System.out.println(customer.getOrders().size());
	}
	
	@Test
	void testMany21SaveBoth() {
		Customer customer = new Customer();
		customer.setName("james");
		
		Order order1 = new Order();
		order1.setName("order1");
		Order order2 = new Order();
		order2.setName("order2");
		
		//设定双向关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		//双向关系默认情况下双发都会维护关联关系，更新数据的时候会更新双方的情况
		//一般都是设置多的一方来维护关联关系（类似于老师和学生相互记名字的情况），这个时候就要在一的
		//映射文件里的set节点配置控制反转 inverse="true"，即一的一方失去控制关系，由多的一方维护双方关系
		//好处就是不会有额外的update语句执行
		//需要先保存一，再保存多（效率更高，否则会多出update语句，因为先插入多会无法确定外键值）
		this.session.save(customer);
//		this.session.save(order1);
//		this.session.save(order2);
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

# “校园微生活”后台接口
#### 框架基本介绍
> 后台采用的是ssh框架编写的，ssh相信大家都知道。Struts主要用来对用户的请求进行拦截，将拦截的信息交给业务逻辑层处理，其核心控制器是ActionServlet；
hibernate是利用hql语言负责数据库与dao层的交互，有五个核心接口：Session、SessionFactory、Configuration（前三个采用工厂模式）、Transaction、Query ；
spring就相当于一个容器，他通过各种bean的配置，整合和管理struts和hibernate,其两大特性：面向切面（AOP）和控制反转(IOC)达到高内聚、松耦合（利用各种注解完成依赖注入）的效果,。

#### 后台相关代码配置
##### 1. struts的拦截配置
	<action name="loginAction_*" method="{1}" class="loginAction">
		<result type="json">
		    <param name="root">Rows</param>
		</result>
	</action>
	<action name="userAction_*" method="{1}" class="userAction">
		<result type="json">
		    <param name="root">Rows</param>
		</result>
	</action>


##### 2. hibernate的数据库相关配置
	alias=proxool-pool
    driverUrl=jdbc:mysql://localhost:3306/graduationproject?useUnicode=true&amp;characterEncoding=utf8
    driver=org.gjt.mm.mysql.Driver
    user=root
    password=123456
    houseKeepingSleepTime=90000
    houseKeepingTestSql=select CURRENT_DATE
    maximumConnectionCount=100
    minimumConnectionCount=10
    simultaneousBuildThrottle=10
    prototypeCount=5
    testBeforeUse=true
    testAfterUse=true
##### 3. spring容器的bean配置
    <context:component-scan base-package="com.zzia.graduation"></context:component-scan>
	<bean
		class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="locations">
			<list>
				<value>classpath:proxool.properties</value>
			</list>
		</property>
	</bean>
	<bean id="dataSource" class="org.logicalcobwebs.proxool.ProxoolDataSource"
		destroy-method="close">
		<property name="driver" value="${driver}" />
		<property name="driverUrl" value="${driverUrl}" />
		<property name="user" value="${user}" />
		<property name="password" value="${password}" />
		<property name="alias" value="${alias}" />
		<property name="houseKeepingSleepTime" value="${houseKeepingSleepTime}" />
		<property name="houseKeepingTestSql" value="${houseKeepingTestSql}" />
		<property name="maximumConnectionCount" value="${maximumConnectionCount}" />
		<property name="minimumConnectionCount" value="${minimumConnectionCount}" />
		<property name="simultaneousBuildThrottle" value="${simultaneousBuildThrottle}" />
		<property name="prototypeCount" value="${prototypeCount}" />
		<property name="testBeforeUse" value="${testBeforeUse}" />
		<property name="testAfterUse" value="${testAfterUse}" />
	</bean>
	<bean id="sessionFactory"
		class="org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="packagesToScan" value="com.zzia.graduation.model" />
		<property name="hibernateProperties">
		  <value>
		    hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
		    hibernate.hbm2ddl.auto=update
		    hibernate.show_sql=false
		    hibernate.format_sql=false
		    hibernate.cache.use_second_level_cache=true
		    hibernate.cache.user_query_cache=false
		    hibernate.cache.provider_class=org.hibernate.cache.EhCacheProvider
		    hibernate.autoReconnect=true
		    hibernate.autoReconnectForPools=true
		    hibernate.is-connection-validation-required=true
		  </value>
	             </property>
    </bean>
##### 4. 请求接口时的相关调用

public String getUserInfo() {

	try {
		String userId = ServletActionContext.getRequest().getParameter("userId");
		if (ParameterUtils.judgeParams(userId)) {
			User user = userService.getUser("userId", userId);
				if (user != null) {
					setRows(PutUtils.success(user));
				} else {
					setRows(PutUtils.empty("该用户已不存在！"));
				}
			} else {
				setRows(PutUtils.parameterError());
			}
		} catch (Exception e) {
			System.out.println(StringUtils.getErrorMsg());
			e.printStackTrace();
		}
		return SUCCESS;
	}
> 最后：ssh框架对于某些开发项目来说可能有些繁琐了，现在快速开发都使用的是ssm，spring boot等，我也正在学习中....
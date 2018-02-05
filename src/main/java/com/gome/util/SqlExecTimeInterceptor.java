package com.gome.util;

import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

/**
 * 数据库操作性能拦截器,记录SQL耗时
 * 
 * @Intercepts定义Signature数组,因此可以拦截多个,但是只能拦截类型为： Executor ParameterHandler
 *                                              StatementHandler
 *                                              ResultSetHandler
 */  
@Intercepts(value = {
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }) })
public class SqlExecTimeInterceptor implements Interceptor {

	private static final Logger logger = Logger.getLogger(SqlExecTimeInterceptor.class);

	/**
	 * 实现拦截的地方
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

		Object parameter = null;
		if (invocation.getArgs().length > 1) {
			parameter = invocation.getArgs()[1];
		}
		Object target = invocation.getTarget();
		Object result = null;
		if (target instanceof Executor) {

			String sqlId = mappedStatement.getId();

			BoundSql sql = mappedStatement.getBoundSql(parameter);

			logger.info(sqlId + "\n execution sql : " + sql.getSql());

			long start = System.currentTimeMillis();
			Method method = invocation.getMethod();
			/** 执行方法 */
			result = invocation.proceed();
			long end = System.currentTimeMillis();
			logger.info("[sql] execute [" + method.getName() + "] cost [" + (end - start) + "] ms");
		}
		return result;
	}

	/**
	 * Plugin.wrap生成拦截代理对象
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

}
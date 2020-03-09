package com.man.config.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.man.common.enums.DBTypeEnum;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan(basePackages = "com.man.mapper", sqlSessionTemplateRef = "managerSqlSessionTemplate") //sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

    // ------------------------------------------------- 多数据源配置 --------------------------------------------

    @Bean
    @ConfigurationProperties("master.datasource")
    public DataSource masterDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("slaver1.datasource")
    public DataSource slave1DataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("slaver2.datasource")
    public DataSource slave2DataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public DataSource myRoutingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                          @Qualifier("slave1DataSource") DataSource slave1DataSource,
                                          @Qualifier("slave2DataSource") DataSource slave2DataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBTypeEnum.MASTER, masterDataSource);
        targetDataSources.put(DBTypeEnum.SLAVE1, slave1DataSource);
        targetDataSources.put(DBTypeEnum.SLAVE2, slave2DataSource);
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }

    // ------------------------------------------------- Druid 监控配置 --------------------------------------------

    @Bean
    public ServletRegistrationBean druidServlet() {// 主要实现web监控的配置处理
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
                new StatViewServlet(), "/druid/*");//表示进行druid监控的配置处理操作
        //servletRegistrationBean.addInitParameter("allow", "127.0.0.1,192.168.202.233");//白名单
        //servletRegistrationBean.addInitParameter("deny", "192.168.202.234");//黑名单
        servletRegistrationBean.addInitParameter("loginUsername", "root");//用户名
        servletRegistrationBean.addInitParameter("loginPassword", "root");//密码
        servletRegistrationBean.addInitParameter("resetEnable", "true");//是否可以重置数据源
        return servletRegistrationBean;

    }

    @Bean    //监控
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");//所有请求进行监控处理
        filterRegistrationBean.addInitParameter("exclusions", "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");//排除
        return filterRegistrationBean;
    }

    // ------------------------------------------------- MyBatis-Plus 配置 --------------------------------------------

    @Bean
    public PlatformTransactionManager platformTransactionManager(@Qualifier("myRoutingDataSource") DataSource myRoutingDataSource) {
        return new DataSourceTransactionManager(myRoutingDataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("myRoutingDataSource") DataSource myRoutingDataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(myRoutingDataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "managerSqlSessionTemplate")
    public SqlSessionTemplate managerSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}

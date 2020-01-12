package com.man.common.annotation;

import org.springframework.data.elasticsearch.annotations.Document;

import java.lang.annotation.*;

/**
 * 用来标注方法，表示此方法使用master数据库
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
//@Inherited
//@Documented
public @interface Master {
}

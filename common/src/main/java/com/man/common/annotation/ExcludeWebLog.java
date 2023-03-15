package com.man.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author mchangx
 * @Description 标注不需要被打印weblog的方法
 * @Date 14:11 2022/8/22
 * @Param
 * @return
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeWebLog
{
}
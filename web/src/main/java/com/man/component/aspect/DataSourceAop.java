package com.man.component.aspect;

import com.man.config.db.DBContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAop {

    @Pointcut("@annotation(com.man.common.annotation.Master) " +
            "|| execution(* com.man.service..*.insert*(..)) " +
            "|| execution(* com.man.service..*.add*(..)) " +
            "|| execution(* com.man.service..*.update*(..)) " +
            "|| execution(* com.man.service..*.edit*(..)) " +
            "|| execution(* com.man.service..*.delete*(..)) " +
            "|| execution(* com.man.service..*.remove*(..))")
    public void writePointcut() {

    }

    @Pointcut("!@annotation(com.man.common.annotation.Master) " +
            "&& (execution(* com.man.service..*.select*(..)) " +
            "|| execution(* com.man.service..*.get*(..)))")
    public void readPointcut() {

    }

    @Before("writePointcut()")
    public void write() {
        DBContext.master();
    }

    @Before("readPointcut()")
    public void read() {
        DBContext.slave();
    }

}

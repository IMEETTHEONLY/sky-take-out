package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component  //交给spring管理
@Slf4j
public class AutoFillAspect {
    //定义切入点表达式

    //匹配mapper下面加了AutoFill的方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    void pt(){}

    //前置通知进行字段填充
    @Before("pt()")
    public void autofill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //首先获取到注解的类型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = annotation.value();

        //获取到参数的实体对象
        Object[] args = joinPoint.getArgs();
        //如果说没有参数 就直接返回 不需要进行字段填充了
        if(args==null||args.length==0){
            return;
        }

        //这里规定好  当参数有多个的时候 第一个参数为实体
        Object entity=args[0];  //这个切入类通用任何对象的插入
        //准备赋值的数据
        LocalDateTime now=LocalDateTime.now();
        Long id= BaseContext.getCurrentId();
        //根据当前的注解类型，用反射将公共字段进行填充
        if(operationType==OperationType.INSERT){
            //处理四个公共填充
            //暴力反射获取set方法进行设置


            //第二个参数是该方法的参数
            Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser",Long.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

            //执行方法
            setCreateTime.invoke(entity,now);
            setCreateUser.invoke(entity,id);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,id);


        }
        else{
            //更新操作 处理两个即可

            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,id);
        }

    }
}

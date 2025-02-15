package com.sky.annotation;

import com.sky.enumeration.OperationType;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)   //加在方法上
@Retention(RetentionPolicy.RUNTIME)  //设定生命周期
public @interface AutoFill {
    OperationType value();  //设定注解的枚举  @AutoFill(value=??之类的)
}

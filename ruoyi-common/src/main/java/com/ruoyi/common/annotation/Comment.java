package com.ruoyi.common.annotation;import java.lang.annotation.ElementType;import java.lang.annotation.Target;@Target(ElementType.FIELD)public @interface Comment {	public String value() default "";}
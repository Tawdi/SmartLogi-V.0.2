package com.smartlogi.smartlogidms.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Searchable {
    String[] fields();
}

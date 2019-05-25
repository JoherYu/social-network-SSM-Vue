package com.sharephoto.utils;

import java.lang.annotation.*;

/**
 * @author Joher
 * @data 2019/5/20
 **/
@Documented
@Target(
        { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface confirmRequired {
}

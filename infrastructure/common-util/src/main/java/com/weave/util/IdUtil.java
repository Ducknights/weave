package com.weave.util;

import cn.hutool.core.lang.Snowflake;

public class IdUtil {
    public static Long snowflakeId(Integer workerId, Integer datacenterId) {
        Snowflake snowflake = new Snowflake(workerId, datacenterId);
        return snowflake.nextId();
    }
}

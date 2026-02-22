package org.example.util;

import cn.hutool.core.lang.Snowflake;
/**
 * ID工具类，提供各种ID生成方法
 */
public class IdUtil {
    /**
     * 使用Snowflake算法生成唯一ID
     * @param workerId 工作机器ID
     * @param datacenterId 数据中心ID
     * @return 生成的唯一ID
     */
    public static Long snowflakeId (Integer workerId, Integer datacenterId) {
        // 创建Snowflake实例并传入工作机器ID和数据中心ID
        Snowflake snowflake = new Snowflake(workerId, datacenterId);
        // 生成并返回下一个ID
        return snowflake.nextId();
    }
}

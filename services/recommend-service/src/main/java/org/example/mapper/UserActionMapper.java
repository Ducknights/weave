package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.model.entity.UserAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserActionMapper extends BaseMapper<UserAction> {

    /**
     * 获取用户最近的交互帖子及加权权重（SQL聚合）
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return List of [postId, weightSum]
     */
    @Select("""
            SELECT target_id AS targetId, SUM(
                CASE type
                    WHEN 1 THEN 3  -- LIKE
                    WHEN 2 THEN 5  -- COLLECT
                    WHEN 3 THEN 1  -- VIEW
                END
            ) AS weightSum
            FROM user_action
            WHERE user_id = #{userId}
              AND type IN (1, 2, 3)
            GROUP BY target_id
            ORDER BY weightSum DESC
            LIMIT #{limit}
            """)
    List<Map<Long, Long>> selectRecentUserInteractions(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 获取热门帖子ID列表（按权重计算热度，SQL聚合）
     * @param since 起始时间
     * @param limit 返回数量限制
     * @return 热门帖子ID列表
     */
    @Select("""
            SELECT target_id AS targetId, SUM(
                CASE type
                    WHEN 1 THEN 3  -- LIKE
                    WHEN 2 THEN 5  -- COLLECT
                    WHEN 3 THEN 1  -- VIEW
                END
            ) AS weightSum
            FROM user_action
            WHERE created_time >= #{since}
              AND type IN (1, 2, 3)
            GROUP BY target_id
            ORDER BY weightSum DESC
            LIMIT #{limit}
            """)
    List<Map<Long, Long>> selectHotPosts(@Param("since") LocalDateTime since, @Param("limit") int limit);

    /**
     * 计算帖子共现对（SQL自连接，直接在数据库完成配对和权重计算）
     * 返回：postA, postB, 共现权重
     * 注意：只返回 postA < postB 的配对避免重复
     */
    @Select("""
            SELECT
                a.target_id AS postA,
                b.target_id AS postB,
                LEAST(
                    CASE a.type WHEN 1 THEN 3 WHEN 2 THEN 5 WHEN 3 THEN 1 END,
                    CASE b.type WHEN 1 THEN 3 WHEN 2 THEN 5 WHEN 3 THEN 1 END
                ) AS coWeight
            FROM user_action a
            INNER JOIN user_action b
                ON a.user_id = b.user_id
                AND a.target_id < b.target_id
            WHERE a.created_time >= #{since}
              AND b.created_time >= #{since}
              AND a.type IN (1, 2, 3)
              AND b.type IN (1, 2, 3)
            """)
    List<Map<String, Object>> selectPostCoOccurrencePairs(@Param("since") LocalDateTime since);

    /**
     * 统计每篇帖子的总权重（用于相似度计算的分母）
     */
    @Select("""
            SELECT target_id AS postId, SUM(
                CASE type
                    WHEN 1 THEN 3  -- LIKE
                    WHEN 2 THEN 5  -- COLLECT
                    WHEN 3 THEN 1  -- VIEW
                END
            ) AS totalWeight
            FROM user_action
            WHERE created_time >= #{since}
              AND type IN (1, 2, 3)
            GROUP BY target_id
            """)
    List<Map<String, Object>> selectPostTotalWeights(@Param("since") LocalDateTime since);

    /**
     * 删除用户行为记录
     */
    void deleteByUserIdAndTargetId(@Param("userId") Long userId, @Param("targetId") Long targetId);
}

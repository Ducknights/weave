package org.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Activity;
import org.example.model.vo.ActivityCardVo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Insert("INSERT INTO activities(club_id,title,date,time,location,description) VALUES(#{clubId},#{title},#{date},#{time},#{location},#{description})")
    Activity creatActivity(Activity activity );

    @Select("DELETE FROM activities WHERE id = #{ActivityId}")
    void deleteActivity(Integer ActivityId);

    @Update({
            "<script>",
            "UPDATE activities",
            "<set>",
            "<if test='clubId != null'>club_id = #{clubId},</if>",
            "<if test='title != null'>title = #{title},</if>",
            "<if test='date != null'>date = #{date},</if>",
            "<if test='time != null'>time = #{time},</if>",
            "<if test='location != null'>location = #{location},</if>",
            "<if test='description != null'>description = #{description},</if>",
            "</set>",
            "WHERE id = #{id}",
            "</script>"
    })
    Activity updateActivity(Activity activity);

    @Select("SELECT " +
            "a.id, " +
            "a.title, " +
            "c.name as club_name, " +
            "a.description, " +
            "a.date, " +
            "a.start_time, " +
            "a.end_time, " +
            "a.location " +
            "FROM activities a left join clubs c " +
            "on a.club_id = c.id " +
            "WHERE a.date >= #{startDate} AND a.date <= #{endDate} " +
            "ORDER BY a.date ASC, a.start_time ASC")
    List<ActivityCardVo> queryActivity(LocalDate startDate, LocalDate endDate);
}
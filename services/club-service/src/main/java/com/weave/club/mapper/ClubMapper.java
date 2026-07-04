package com.weave.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.weave.club.model.entity.Club;
import com.weave.club.model.vo.ClubCardVo;

import java.util.List;

@Mapper
public interface ClubMapper extends BaseMapper<Club> {

    @Select("SELECT " +
            "c.id, " +
            "c.name, " +
            "c.description, " +
            "COUNT(m.id) as memberCount " +
            "FROM clubs c " +
            "LEFT JOIN members m ON c.id = m.club_id " +
            "GROUP BY c.id")
    List<ClubCardVo> queryClubs();

    @Select("SELECT * FROM clubs WHERE id = #{clubId}")
    Club getClubById(Integer clubId);

    @Select("SELECT " +
            "c.id, " +
            "c.name, " +
            "c.description, " +
            "COUNT(m.id) as memberCount " +
            "FROM clubs c " +
            "LEFT JOIN members m ON c.id = m.club_id " +
            "WHERE c.id = #{clubId}")
    ClubCardVo getClubVoById(Integer clubId);
}
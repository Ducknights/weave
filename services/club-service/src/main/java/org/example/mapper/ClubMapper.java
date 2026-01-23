package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Club;
import org.example.model.vo.ClubCardVo;

import java.util.List;

@Mapper
public interface ClubMapper extends BaseMapper<Club> {
    @Insert("INSERT INTO clubs(name,description) VALUES(#{name},#{description})")
    Club createClub(Club club);

    @Select("DELETE FROM clubs WHERE id = #{clubId}")
    void deleteClub(Integer clubId);

    @Update({
            "<script>",
            "UPDATE clubs",
            "<set>",
            "<if test='name != null'>name = #{name},</if>",
            "<if test='description != null'>description = #{description},</if>",
            "</set>",
            "WHERE id = #{id}",
            "</script>"
    })
    Club updateClub(Club club);

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
}
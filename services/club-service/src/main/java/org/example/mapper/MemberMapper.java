package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Member;

import java.util.List;

@Mapper
public interface MemberMapper {

    @Insert("INSERT INTO members(user_id, club_id, role, status) VALUES(#{userId}, #{clubId}, #{role}, #{status})")
    Member createMember(Member member);

    @Select("DELETE FROM members WHERE id = #{memberId}")
    void deleteMember(Integer memberId);

    @Update({
            "",
            "UPDATE members",
            "<set>",
            "<if test='userId != null'>user_id = #{userId},</if>",
            "<if test='clubId != null'>club_id = #{clubId},</if>",
            "<if test='role != null'>role = #{role},</if>",
            "<if test='status != null'>status = #{status},</if>",
            "</set>",
            "WHERE id = #{id}",
            "</script>"
    })
    Member updateMember(Member member);

    @Select("SELECT * FROM members")
    List<Member> queryMembers();

    @Select("SELECT * FROM members WHERE id = #{memberId}")
    Member getMemberById(Integer memberId);

    @Select("SELECT * FROM members WHERE club_id = #{clubId}")
    List<Member> getMembersByClubId(Integer clubId);

    @Select("SELECT * FROM members WHERE user_id = #{userId}")
    List<Member> getMembersByUserId(Integer userId);
}
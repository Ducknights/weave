package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.model.entity.UserInfo;
import org.example.model.vo.UserInfoVo;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    default int updateInfo(UserInfo user){
        return updateById(user);
    }

    @Select("SELECT " +
            "u.id, u.name, u.avatar, u.gender, u.motto, " +
            // 统计关注数：以当前用户为 user_id 的记录数
            "(SELECT COUNT(*) FROM user_relations WHERE user_id = u.id and type = 1) as followCont, " +
            // 统计粉丝数：以当前用户为 target_id 的记录数
            "(SELECT COUNT(*) FROM user_relations WHERE target_id = u.id and type = 1) as fansCont " +
            "FROM user_info u " +
            "WHERE u.id = #{id}")
    UserInfoVo selectUserInfoById(Long id);
}
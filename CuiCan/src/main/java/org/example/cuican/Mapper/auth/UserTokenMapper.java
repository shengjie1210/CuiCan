package org.example.cuican.Mapper.auth;
/**
 * UserTokenMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.auth.UserToken;

//@Mapper
//public interface UserTokenMapper {
//
//    void insert(UserToken token);
//}
@Mapper
public interface UserTokenMapper {

    void insert(UserToken token);

    UserToken selectByToken(@Param("token") String token);

    void invalidateByUserId(@Param("userId") Long userId);

    void invalidateByToken(@Param("token") String token);
}
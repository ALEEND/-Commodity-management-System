package com.mko.cms.repository;

import com.mko.cms.enetity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {

    @Query(value="select *from user WHERE tel=?1",nativeQuery = true)
    UserInfo findByTel(String tel);

    //JAP :UserInfo findByTel(String tel);

    @Query(value ="select *from user WHERE userID=?1",nativeQuery = true)
    UserInfo chooseID(Integer userID);

   //JPA: UserInfo findByUserID(Integer userID);

    @Transactional
    @Modifying //定义更新删除操作
    @Query(value = "update user set password =?1 where userID=?2",nativeQuery = true)
    UserInfo updatePassword(String password, Integer userID);


}

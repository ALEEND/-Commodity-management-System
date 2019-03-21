package com.mko.cms.repository;

import com.mko.cms.enetity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {

    @Query(value="select *from user WHERE tel=?1",nativeQuery = true)
    UserInfo findByTel(String tel);

    @Query(value ="select *from user WHERE userID=?1",nativeQuery = true)
    UserInfo chooseID(Integer userID);

    @Query(value = "update user set password =?1 where userID=?2",nativeQuery = true)
    UserInfo updatePassword(String password, Integer userID);

//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE pms SET gmtCreate = ?2 WHERE tel = ?1", nativeQuery = true)
//    void updategmtCreate(String tel, Date gmtCreate);




}

package com.mko.cms.repository;

import com.alibaba.fastjson.JSONObject;
import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Integer> {

    @Query(value="SELECT * FROM WHERE cartId=?1",nativeQuery = true)
    Cart cartId(Integer cartId);

   // JPA 规范 Cart findByCartId(Integer cartID);

    @Transactional
    @Modifying //定义更新删除操作
    @Query(value = "DELETE FROM cart WHERE userID=?1",nativeQuery = true)
    void findqk(Integer userID);

    @Query(value = "select * from cart where userID=?1",nativeQuery = true)
    List<Cart> getCartList(Integer userID);
    // JPA 规范 List<Cart> findByUserID(Integer userID);
}

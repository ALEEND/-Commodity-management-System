package com.mko.cms.repository;

import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    @Query(value = "select * from goods where goodsId=?1",nativeQuery = true)
    Cart findgoods(Integer goodsId);

    @Query(value = "select * from user where userID=?1",nativeQuery = true)
    Cart finduser(Integer userID);

    @Query(value = "SELECT  goods.goodsName  FROM cart,goods where goods.goodsId =?1",nativeQuery = true)
    Cart fingName(Integer goodsId);

    @Query(value="SELECT * FROM WHERE cartId=?1",nativeQuery = true)
    Cart cartId(Integer cartId);

    @Query(value = "DELETE FROM cart WHERE userID=?1",nativeQuery = true)
    void findqk(Integer userID);


    @Query(value = "select * from cart where userID=?1",nativeQuery = true)
    List<Cart> getCartList(Integer userID);

    @Query(value = "select * from cart where userID=?1",nativeQuery = true)
    Cart getCartbuy(Integer userID);

}

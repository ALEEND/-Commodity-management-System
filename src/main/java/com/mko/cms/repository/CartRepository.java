package com.mko.cms.repository;

import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    @Query(value = "select *from cart where goodsId=?1",nativeQuery = true)
    Cart findgoods(Integer gooodsId);

    @Query(value = "SELECT  goods.goodsName  FROM cart,goods where goods.gooodsId = cart.goodsId=?1",nativeQuery = true)
    Cart fingName(Integer goodsId);

    @Query(value="SELECT * FROM WHERE cartId=?1",nativeQuery = true)
    Cart cartId(Integer cartid);

    @Query(value = "SELECT SUM(goods.price * cart.quantity)  FROM goods, cart WHERE cart.goodsId = goods.goodsId AND userID = userID =?1 GROUP BY goods.goodsId",nativeQuery = true)
    List<Cart> findpp( Integer userID);
}

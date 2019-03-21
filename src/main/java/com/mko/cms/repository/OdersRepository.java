package com.mko.cms.repository;

import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author Yuxz
 * @date 2019-03-13 18:13
 */
public interface OdersRepository extends JpaRepository<Orders,Integer> {

    @Query(value = "select * from orders where userID=?1",nativeQuery = true)
    List<Orders> findOlist(Integer userID);

    //jpa规范
    List<Orders> findByUserID(Integer userID);

    @Query(value = "select goods.goodsName,orders.ordersCode,orders.totalPrice,orders.ordersQuantity,orders.ordersdate from goods,orders where goods.goodsId=orders.goodsId and orders.userID=?1",nativeQuery = true)
    List<Map<String,Object>> getInfo(Integer userID);
}

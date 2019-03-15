package com.mko.cms.repository;

import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Yuxz
 * @date 2019-03-13 18:13
 */
public interface GoodsRepository extends JpaRepository<Goods,Integer> {

    @Query(value ="select *from goods WHERE goodsId=?1",nativeQuery = true)
    Goods chooseGoodsId(Integer goodsId);
    @Query(value = "select * from goods where goodsId=?1",nativeQuery = true)
    Goods getGoodPrice(Integer goodsId);


}

package com.mko.cms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import com.mko.cms.repository.CartRepository;
import com.mko.cms.repository.GoodsRepository;
import com.mko.cms.repository.OdersRepository;
import com.mko.cms.util.MKOResponse;
import com.mko.cms.util.MKOResponseCode;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yuxz
 * @date 2019-03-13 18:11
 */
@RestController
@RequestMapping({"cart"})
public class CartController extends BaseController {
    private GoodsRepository goodsRepository;
    private OdersRepository odersRepository;
    private CartRepository cartRepository;
    @PostMapping("addToCart")
    MKOResponse addToCart(@RequestBody Cart cartData,@RequestParam Integer userID){
        try{
            if(cartData.getUserID().equals("")||cartData.getGoodsId().equals("")) {
                return makeParamsLackResponse("缺少UserID或goodsId");
            }
            Cart result=cartRepository.findgoods(cartData.getCartId());
            if(result==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到goodsId");
            }
            Cart cart=new Cart();
            cart.setQuantity(cartData.getQuantity()==null? 1:cartData.getQuantity());
            cart.setGoodsId(cartData.getGoodsId());
            cart.setUserID(cartData.getUserID());
            cart.setCartDate(new Date());
            this.cartRepository.saveAndFlush(cart);
            List<Cart> price=cartRepository.findpp(userID);
            double priceSum=price.stream().count();
            cart.setAllPrice(priceSum);
            cartRepository.saveAndFlush(cart);
            return makeSuccessResponse("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return makeBussessErrorResponse("未知异常");
        }
    }


    @PostMapping ("updateCart")
    MKOResponse updateCart(@RequestParam Cart cartData,@RequestParam Integer userID){
            Cart cart=new Cart();
            cart.setGoodsId(cartData.getGoodsId());
            cart.setQuantity(cartData.getQuantity());
            cart.setCgmtModifeid(new Date());
            cartRepository.saveAndFlush(cart);
            List<Cart> price=cartRepository.findpp(userID);
            double priceSum=price.stream().count();
            cart.setAllPrice(priceSum);
            cartRepository.saveAndFlush(cart);
            return makeSuccessResponse("");

//            String price=cartRepository.findpp(a).toString();
//            double p= Double.parseDouble(price);
//            int q=Integer.parseInt(cartData.getQuantity().toString());
//            double all=p*q;
//            cart.setAllPrice(all);
//            cartRepository.saveAndFlush(cart);
//            return  makeSuccessResponse("修改成功");

    }
    @GetMapping("deleteCart")
    MKOResponse delete(@RequestParam Integer cartId,@RequestParam Integer userID) {
        {
            Cart result = cartRepository.cartId(cartId);
            if (result == null) {
                return this.makeResponse(MKOResponseCode.DataNotFound, "找不到此ID");
            } else {
                Cart cart=new Cart();
                this.cartRepository.delete(result);
                List<Cart> price=cartRepository.findpp(userID);
                DoubleSummaryStatistics collect=price.stream().collect(Collectors.summarizingDouble(value -> value));
                double priceSum=collect.getSum();
                cart.setAllPrice(priceSum);
                cartRepository.saveAndFlush(cart);
                return this.makeSuccessResponse("删除成功");
            }
        }
    }

    @GetMapping("listCart")
    MKOResponse updateCart(@RequestParam Integer UserID,
                           @RequestParam int page, @RequestParam int count) {
        Optional<Cart> cart = cartRepository.findById(UserID);
        if (!cart.isPresent()) {
            return makeResponse(MKOResponseCode.DataNotFound, "找不到数据");
        }
        StringBuilder sqlCount = new StringBuilder("select count(*) count from goods,cart where 1=1");
        StringBuilder sql = new StringBuilder("select goods.goodsName,goods.price,cart.quantity from goods,cart where 1=1");
        String condition = " AND  (cart.goodsId=goods.goodsId)";
        if(UserID!=null){
            condition+= String.format(" AND UserID = '%s'",UserID);
        }
        Query queryCount = entityManager.createNativeQuery(sqlCount.toString());
        //遍历
        sql.append("ORDER BY id DESC ");
        //分页
        sql.append("    LIMIT " + (page - 1) * count + "," + count);
        Query query = entityManager.createNativeQuery(sql.toString());
        Map<String, Object> result = (Map<String, Object>) queryCount.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
        int total = Integer.parseInt(result.get("count").toString());
        List list = query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        Object resultAll = ListToString(list, page, count, total);
        return makeResponse(MKOResponseCode.Success, resultAll, "");
    }


    /*
     * 列表数据
     * */
    public Object ListToString(List list,int page,int count ,int countNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        //当前页数
        map.put("page", page);
        //总页数
        if (count == 0) {
            map.put("pageCount", count);
        } else {
            map.put("pageCount", (countNumber - 1) / count + 1);
        }
        //每页条数
        map.put("count", count);
        //总数
        return  map;
    }


}

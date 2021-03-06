package com.mko.cms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import com.mko.cms.enetity.Orderitem;
import com.mko.cms.enetity.Orders;
import com.mko.cms.repository.*;
import com.mko.cms.util.MKOResponse;
import com.mko.cms.util.MKOResponseCode;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: goods manager system
 * @description: 购物车模块
 * @author: Yuxz
 * @create: 2019-03-20
 **/
@RestController
@RequestMapping({"cart"})
public class CartController extends BaseController {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private OdersRepository odersRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private OrderitemRepository orderitemRepository;


    /**
     * @program: goods manager system
     * @description: 添加购物车
     * @author: Yuxz
     * @create: 2019-03-20
     **/
    @PostMapping("addToCart")
    MKOResponse addToCart(@RequestBody Cart cartData){
        try{
            if(cartData.getGoodsId()==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到[goodsId]参数");
            }
            if(cartData.getUserID()==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到[userId]参数");
            }
            if(userInfoRepository.chooseID(cartData.getUserID())==null){
                return makeResponse(MKOResponseCode.DataNotFound,"无此用户");
            }
            if(goodsRepository.chooseGoodsId(cartData.getGoodsId())==null){
                return makeResponse(MKOResponseCode.DataNotFound,"无此商品");
            }
            Cart cart=new Cart();
            cart.setQuantity(cartData.getQuantity()==null? 1:cartData.getQuantity());
            cart.setGoodsId(cartData.getGoodsId());
            cart.setUserID(cartData.getUserID());
            cart.setCartDate(new Date());
            cart.setCgmtModifeid(new Date());
            cartRepository.saveAndFlush(cart);
            Double sum=totalPrice(cartData.getUserID());
            cart.setAllPrice(sum);
            cartRepository.saveAndFlush(cart);
            return makeSuccessResponse("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return makeBussessErrorResponse("未知异常");
        }
    }


    /**
     * @program: goods manager system
     * @description: 更新购物车
     * @author: Yuxz
     * @create: 2019-03-20
     **/
    @PostMapping ("updateCart")
    MKOResponse updateCart(@RequestBody Cart cartData){
            if(cartData.getUserID()==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到[userID]参数");
            }
            if(cartData.getGoodsId()==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到[goodsId参数");
            }
            if(cartData.getCartId()==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到[cartId]参数");
            }
            if(userInfoRepository.chooseID(cartData.getUserID())==null){
                return makeResponse(MKOResponseCode.DataNotFound,"无此用户");
            }
            if(goodsRepository.chooseGoodsId(cartData.getGoodsId())==null){
                return makeResponse(MKOResponseCode.DataNotFound,"无此商品");
            }
            Cart cart=cartRepository.cartId(cartData.getCartId());
            cart.setCartId(cartData.getCartId());
            cart.setGoodsId(cartData.getGoodsId());
            cart.setQuantity(cartData.getQuantity());
            cart.setUserID(cartData.getUserID());
            cart.setCgmtModifeid(new Date());
            //计算总价格
            cartRepository.saveAndFlush(cart);
            Double sum=totalPrice(cartData.getUserID());
            cart.setAllPrice(sum);
            cartRepository.saveAndFlush(cart);
            return makeSuccessResponse("");
    }


    /**
     * @program: goods manager system
     * @description: 删除购物车
     * @author: Yuxz
     * @create: 2019-03-20
     **/
    @GetMapping("deleteCart")
    MKOResponse delete(@RequestParam Integer cartId,@RequestParam Integer userID) {
        {
            Cart result = cartRepository.cartId(cartId);
            if (result == null) {
                return this.makeResponse(MKOResponseCode.DataNotFound, "找不到此ID");
            }
            if(result.getUserID()!=userID){
                return this.makeResponse(MKOResponseCode.NoPermission,"无权限删除");
            }
            else {
                this.cartRepository.delete(result);
                return this.makeSuccessResponse("删除成功");
            }
        }
    }


    /**
     * @program: goods manager system
     * @description:清空购物车
     * @author: Yuxz
     * @create: 2019-03-20
     **/
    @GetMapping("delAll")
    MKOResponse delAll(@RequestParam Integer userID){
        //查询用户的购物车
       if(userInfoRepository.chooseID(userID)==null){
           return this.makeResponse(MKOResponseCode.DataNotFound,"找不到此用户");
       }
       List<Cart> cart=cartRepository.getCartList(userID);
       if(cart==null){
           return  this.makeResponse(MKOResponseCode.DataNotFound,"找不到购物车");
       }
       cartRepository.findqk(userID);
       return this.makeSuccessResponse("删除成功");
    }


    /**
     * @program: goods manager system
     * @description: 购物车列表
     * @author: Yuxz
     * @create: 2019-03-20
     **/
    @GetMapping("listCart")
    MKOResponse updateCart(@RequestParam Integer userID,
                           @RequestParam int page, @RequestParam int count) {
        List<Cart> cart=cartRepository.getCartList(userID);
        if (cart==null) {
            return makeResponse(MKOResponseCode.DataNotFound, "找不到数据");
        }
        StringBuilder sqlCount = new StringBuilder("select count(*) count from goods,cart where 1=1");
        StringBuilder sql = new StringBuilder("select goods.goodsName,goods.goodsPrice,cart.quantity from goods,cart where 1=1");
        String condition = " AND  cart.goodsId=goods.goodsId";
        condition+= String.format(" AND UserID = '%s'",userID);
        sql.append(condition);
        sqlCount.append(condition);
        Query queryCount = entityManager.createNativeQuery(sqlCount.toString());
        //遍历
        sql.append(" ORDER BY cartId DESC ");
        //分页
        sql.append("    LIMIT " + (page - 1) * count + "," + count);
        Query query = entityManager.createNativeQuery(sql.toString());
        Map<String, Object> result = (Map<String, Object>) queryCount.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
        int total = Integer.parseInt(result.get("count").toString());
        List list = query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        Object resultAll = ListToString(list, page, count, total);
        return makeResponse(MKOResponseCode.Success, resultAll, "");
    }


    /**
     * @program: goods manager system
     * @description: 购买购物车
     * @author: Yuxz
     * @create: 2019-03-20
     **/
    @GetMapping("buyCart")
    public MKOResponse bugCart(@RequestParam Integer userID){
        try{
            //判断客户存在
            if(userInfoRepository.chooseID(userID)==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到此用户");
            }
            List<Cart> cart=cartRepository.getCartList(userID);
            Orderitem orderitem=new Orderitem();
            Orders orders=new Orders();
            orderitem.setOrderId(orders.getOrdersId());
            orders.setUserID(userID);
            orders.setOrdersdate(new Date());
            orders.setOgmtModifeid(new Date());
            for(int i=0;i<cart.size();i++){
//                JSONObject obj = (JSONObject) JSONObject.toJSON(cart.get(1));
                Cart result=cart.get(i);
               orderitem.setQuantity(result.getQuantity());
               orderitem.setGoodsId(result.getGoodsId());
               orderitem.setQuantity(result.getQuantity());
               orderitem.setSubTotal(result.getAllPrice());
               orderitem.setGmtCreate(new Date());
            }
            this.odersRepository.saveAndFlush(orders);
            this.orderitemRepository.saveAndFlush(orderitem);
            return makeSuccessResponse("");
        }catch (Exception e){
            e.printStackTrace();
            return makeBussessErrorResponse("未知异常");
        }
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
        map.put("datas", list);
        return  map;
    }


    public double totalPrice(Integer id){
        List<Cart> list = cartRepository.getCartList(id);
        double sum=0;
        for (int i = 0 ; i<list.size();i++) {
                Cart cart1 = (Cart) list.get(i);
                Goods goods = goodsRepository.getGoodPrice(cart1.getGoodsId());
                sum = cart1.getQuantity() * goods.getGoodsPrice() + sum;
            }
           return sum;
    }

}

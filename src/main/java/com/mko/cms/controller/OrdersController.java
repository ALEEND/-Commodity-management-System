package com.mko.cms.controller;

import com.alibaba.fastjson.JSONObject;
import com.mko.cms.enetity.Cart;
import com.mko.cms.enetity.Goods;
import com.mko.cms.enetity.Orders;
import com.mko.cms.repository.GoodsRepository;
import com.mko.cms.repository.OdersRepository;
import com.mko.cms.repository.OrderitemRepository;
import com.mko.cms.repository.UserInfoRepository;
import com.mko.cms.util.MKOResponse;
import com.mko.cms.util.MKOResponseCode;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: goods manager system
 * @description: 订单管理模块
 * @author: Yuxz
 * @create: 2019-03-18
 **/
@RestController
@RequestMapping({"orders"})
public class OrdersController extends  BaseController {
    @Autowired
    private OdersRepository odersRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private OrderitemRepository  orderitemRepository;

    /**
     * @program: goods manager system
     * @description:  添加订单(单件）
     * @author: Yuxz
     * @create: 2019-03-21
     **/

    @PostMapping("addOrders")
    public MKOResponse addOrders(@RequestBody Orders ordersData) {
        try {
            //判断商品是否存在
            if (ordersData.getGoodsId() == null) {
                return this.makeResponse(MKOResponseCode.ParamsLack, "商品不能为空");
            }
            if(ordersData.getUserID()==null){
                return this.makeResponse(MKOResponseCode.DataNotFound,"缺少userID");
            }
            if(userInfoRepository.chooseID(ordersData.getUserID())==null){
                return this.makeResponse(MKOResponseCode.DataNotFound,"无此用户");
            }
            Goods goods=goodsRepository.chooseGoodsId(ordersData.getGoodsId());
            if(goods==null){
                return this.makeResponse(MKOResponseCode.DataNotFound,"无此商品");
            }

            //判断商品销售状态
            if(!ordersData.getOrdersState().equals(1)){
                return this.makeResponse(MKOResponseCode.NoPermission,"此商品未开售");
            }
            Orders orders = new Orders();
            orders.setUserID(ordersData.getUserID());
            orders.setOrdersQuantity(ordersData.getOrdersQuantity() == null ? 1 : ordersData.getOrdersQuantity());
            orders.setGoodsId(ordersData.getGoodsId());
            //生成订单编号
            String ordersCode=getOrderIdByTime();
            orders.setOrdersCode(ordersCode);
            Double price = goodsRepository.findprice(ordersData.getGoodsId());
            int quantity = ordersData.getOrdersQuantity();
            Double totalPrice = goods.getGoodsPrice() * quantity;
            orders.setTotalPrice(totalPrice);
            orders.setOrdersdate(new Date());
            orders.setOgmtModifeid(new Date());
            odersRepository.saveAndFlush(orders);
            return makeSuccessResponse("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }

    }



    /**
     * @program: goods manager system
     * @description: 订单列表
     * @author: Yuxz
     * @create: 2019-03-21
     **/
    @RequestMapping({"ordersList"})
    public MKOResponse ordersList(@RequestParam Integer userID,
                                  @RequestParam int page,
                                  @RequestParam int count) {
        try {
            List<Orders> orders = odersRepository.findOlist(userID);
            //判断ID
            if (orders==null) {
                return makeResponse(MKOResponseCode.DataNotFound, "找不到数据");
            }
            if(userInfoRepository.chooseID(userID)==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到此用户");
            }
            //查询订单
            StringBuilder sqlCount = new StringBuilder("select count(*) count from orders where 1=1");
            StringBuilder sql = new StringBuilder("select goods.goodsName,goods.goodsPrice,orders.ordersQuantity,orders.totalPrice from goods,orders where 1=1");
            String condition = " AND  ( orders.goodsId = goods.goodsId )";
            condition += String.format(" AND UserID = '%s'", userID);
            Query queryCount = entityManager.createNativeQuery(sqlCount.toString());
            sql.append(condition);
            sqlCount.append(condition);
            //遍历
            sql.append(" ORDER BY ordersId DESC ");
            //分页
            sql.append("    LIMIT " + (page - 1) * count + "," + count);
            Query query = entityManager.createNativeQuery(sql.toString());
            Map<String, Object> result = (Map<String, Object>) queryCount.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
            int total = Integer.parseInt(result.get("count").toString());
            List list = query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
            Object resultAll = ListToString(list, page, count, total);
            return makeResponse(MKOResponseCode.Success, resultAll, "");
        } catch (Exception e) {
            e.printStackTrace();
            return this.makeBussessErrorResponse("未知异常");
        }
    }


    /**
     * @program: goods manager system
     * @description:  订单详情
     * @author: Yuxz
     * @create: 2019-03-21
     **/
    @GetMapping("ordersInfo")
    public MKOResponse ordersInfo(@RequestParam Integer userID) {
        try
          {
              if(userInfoRepository.chooseID(userID)==null){
                  return makeResponse(MKOResponseCode.DataNotFound,"找不到此用户");
              }
              List<Orders> order = odersRepository.findOlist(userID);
              //判断ID
              if (order==null) {
                  return makeResponse(MKOResponseCode.DataNotFound, "找不到数据");
              }
                List<Map<String,Object>> orders=odersRepository.getInfo(userID);
                return makeSuccessResponse(orders);
             }catch(Exception e)
            {
                e.printStackTrace();
                return makeBussessErrorResponse("未知错误");
             }
}

    /**
     * @program: goods manager system
     * @description:  订单删除
     * @author: Yuxz
     * @create: 2019-03-21
     **/
    @GetMapping("ordersDelete")
    public MKOResponse ordersDelete(@RequestParam Integer ordersId,@RequestParam Integer userID){
        try {
            Optional<Orders> orders = this.odersRepository.findById(ordersId);
            if(userInfoRepository.chooseID(userID)==null){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到此用户");
            }
             if (!orders.isPresent()) {
            return this.makeResponse(MKOResponseCode.DataNotFound, "找不到此ID");
            }
            if(orders.get().getUserID()!=userID){
                return this.makeResponse(MKOResponseCode.DataNotFound,"找不到数据");
            }
        else {
            this.odersRepository.delete(orders.get());
            return this.makeSuccessResponse("删除成功");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return this.makeBussessErrorResponse("未知异常");
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
        map.put("data",list);
        return  map;
    }

    //订单编号的自动生成
    public static String getOrderIdByTime() {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            String newDate=sdf.format(new Date());
            String result="";
            Random random=new Random();
            for(int i=0;i<3;i++){
                result+=random.nextInt(10);
             }
             return newDate+result;
    }
}
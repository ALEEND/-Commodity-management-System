//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mko.cms.controller;

import com.alibaba.fastjson.JSONObject;
import com.mko.cms.controller.BaseController;
import com.mko.cms.enetity.Goods;
import com.mko.cms.repository.GoodsRepository;
import com.mko.cms.util.MKOResponse;
import com.mko.cms.util.MKOResponseCode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"goods"})
public class GoodsController extends BaseController {
    @Autowired
    private GoodsRepository goodsRepository;


    @RequestMapping({"info"})
    public MKOResponse info(@RequestParam Integer goodsId) {
        try {
            Optional<Goods> goodsResult = this.goodsRepository.findById(goodsId);
            if (!goodsResult.isPresent()) {
                return this.makeResponse(MKOResponseCode.DataNotFound, "", ",找不到此ID");
            } else {
                JSONObject jsonObject = (JSONObject)JSONObject.toJSON(goodsResult.get());
                jsonObject.remove("goods_category");
                return this.makeSuccessResponse(jsonObject);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            return this.makeBussessErrorResponse("未知错误");
        }
    }

    //             Map<String, Object> goodsParam = new HashMap();
//            String fields = "*";
//            Cart name=cartRepository.fingName(UserID);
//            String condition=String.format("AND userID = %s",UserID);
//            String orderBy = "ORDER BY goodsId DESC";
//            Object result = this.getRecord(fields, "cart", condition, goodsParam, orderBy, page, count);
//            return this.makeSuccessResponse(result);

    @RequestMapping({"list"})
    public MKOResponse list(@RequestParam(defaultValue = "") String goodName,
                            @RequestParam(defaultValue = "") String goodDescribe,
                            @RequestParam(defaultValue = "") String sPrice,
                            @RequestParam(defaultValue = "") String ePrice,
                            @RequestParam int page, @RequestParam int count) {
        try {
            Map<String, Object> goodsParam = new HashMap();
            String fields = "*";
            String condition = "";
            if (!goodName.equals("")) {
                condition = condition + String.format(" AND goodsName like '%s'", goodName);
            }

            if (!goodDescribe.equals("")) {
                condition = condition + String.format(" AND goodsDescribe like '%s'", goodDescribe);
            }

            if (!sPrice.equals("") || !ePrice.equals("")) {
                condition = condition + String.format(" AND goodsPrice between '%s' and '%s'", sPrice, ePrice);
            }

            String orderBy = "ORDER BY goodsId DESC";
            Object result = this.getRecord(fields, "goods", condition, goodsParam, orderBy, page, count);
            return this.makeSuccessResponse(result);
        } catch (Exception var12) {
            var12.printStackTrace();
            return this.makeBussessErrorResponse("未知异常");
        }
    }

    @GetMapping({"delete"})
    MKOResponse delete(@RequestParam Integer goodsId) {
        try {
            Optional<Goods> goodsResult = this.goodsRepository.findById(goodsId);
            if (!goodsResult.isPresent()) {
                return this.makeResponse(MKOResponseCode.DataNotFound, "找不到此ID");
            } else {
                this.goodsRepository.delete(goodsResult.get());
                Goods goods=new Goods();
                goods.setGgmtModifeid(new Date());
                return this.makeSuccessResponse("删除成功");
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            return this.makeBussessErrorResponse("未知异常");
        }
    }

    @PostMapping({"add"})
    MKOResponse add(@RequestBody Goods goodsData) {
        try {
            if (goodsData.getGoodsName() == null) {
                return this.makeResponse(MKOResponseCode.ParamsLack, "商品不能为空");
            } else {
                Goods goods = new Goods();
                goods.setGoodsName(goodsData.getGoodsName());
                goods.setCatalogId(goodsData.getCatalogId());
                goods.setGoodsDescribe(goodsData.getGoodsDescribe());
                goods.setGoodsPicture(goodsData.getGoodsPicture());
                goods.setGoodsPrice(goodsData.getGoodsPrice());
                goods.setGoodsState(goodsData.getGoodsState() == null ? 0 : goodsData.getGoodsState());
                goods.setGgmtCreate(new Date());
                this.goodsRepository.saveAndFlush(goods);
                return this.makeSuccessResponse("添加成功");
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            return this.makeBussessErrorResponse("未知异常");
        }
    }

    @PostMapping({"update"})
    MKOResponse update(@RequestBody Goods goodsData) {
        try {
            if (goodsData.getGoodsName() == null) {
                return this.makeResponse(MKOResponseCode.ParamsLack, "商品名字不能为空");
            } else if (goodsData.getGoodsId() != null && goodsData.getGoodsId() > 0) {
                Goods updateResult = this.goodsRepository.chooseGoodsId(goodsData.getGoodsId());
                if (updateResult == null) {
                    return this.makeResponse(MKOResponseCode.DataNotFound, "id不能为空");
                } else {
                    Goods goods = new Goods();
                    goods.setGoodsId(goodsData.getGoodsId());
                    goods.setGoodsName(goodsData.getGoodsName());
                    goods.setCatalogId(goodsData.getCatalogId());
                    goods.setGoodsDescribe(goodsData.getGoodsDescribe());
                    goods.setGoodsPicture(goodsData.getGoodsPicture());
                    goods.setGoodsPrice(goodsData.getGoodsPrice());
                    goods.setGoodsState(goodsData.getGoodsState() == null ? 0 : goodsData.getGoodsState());
                    goods.setGgmtModifeid(new Date());
                    this.goodsRepository.saveAndFlush(goods);
                    return this.makeSuccessResponse("修改成功");
                }
            } else {
                return this.makeParamsLackResponse("");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            return this.makeBussessErrorResponse("位置异常");
        }
    }
}

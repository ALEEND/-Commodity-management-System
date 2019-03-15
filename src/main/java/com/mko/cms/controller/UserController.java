package com.mko.cms.controller;

import com.alibaba.fastjson.JSONObject;
import com.mko.cms.enetity.UserInfo;
import com.mko.cms.repository.UserInfoRepository;
import com.mko.cms.util.MD5Util;
import com.mko.cms.util.MKOResponse;
import com.mko.cms.util.MKOResponseCode;
import com.mko.cms.util.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;
import java.util.*;

/**
 * @program: person manager system
 * @description: 用户功能模块
 * @author: Yuxz
 * @create: 2019-03-07
 **/
@RequestMapping(value = "pms")
@RestController

//8个接口

public class UserController extends BaseController {
    @Autowired
    private UserInfoRepository personRepository;
    @Autowired
    StringRedisTemplate redisTemplate;
    /**
     * @program: goods manager system
     * @description: 用户客服登陆模块
     * @author: Yuxz
     * @create: 2019-03-07
     **/
    @GetMapping("login")
    MKOResponse login(@RequestParam String tel, @RequestParam String password) {
        try {
            UserInfo userInfo = personRepository.findByTel(tel);
            //验证用户是否存在
            if (userInfo == null) {
                return this.makeResponse(MKOResponseCode.DataNotFound, "用户不存在");
            }
            //验证密码
            if (!userInfo.getPassword().equals(password)) {
                return this.makeBussessErrorResponse("密码错误");
            }
            //验证停用与禁用
            String token = this.refreshToken(userInfo);
            JSONObject obj = (JSONObject) JSONObject.toJSON(userInfo);
            obj.remove("password");
            JSONObject result = new JSONObject();
            result.put("userInfo", obj);
            result.put("token", token);
            return this.makeSuccessResponse(result);
//            System.out.println(token);


        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("其他异常，登录失败！");
        }
    }


    /**
     * @program: goods manager system
     * @description: 客户列表模块
     * @author: Yuxz
     * @create: 2019-03-07
     **/
    @GetMapping("list")
    MKOResponse list(@RequestParam(defaultValue = "") String nameAndTel,
                     @RequestParam(defaultValue = "-1") Integer state,
                     @RequestParam int count,@RequestParam int page) {
        try {
            StringBuilder sqlCount=new StringBuilder("select count(*) count from user where 1=1");
            StringBuilder sql=new StringBuilder("select id,name,sex,tel,age,state,role,gmtcreate from user where 1=1");
            //筛选
            String condition = " ";
            if (-1 != state) {
                condition = condition + ("AND state = " + state + " ");
            }
            if (!nameAndTel.equals("")|| nameAndTel.length()==0) {
                condition = condition + "AND (name like '%" + nameAndTel + "%' OR tel like '%" + nameAndTel + "%' ) ";
            }
            if(!condition.isEmpty()){
                sqlCount.append(condition);
                sql.append((condition));
            }
            Query queryCount=entityManager.createNativeQuery(sqlCount.toString());
            //遍历
            sql.append("ORDER BY id DESC ");
            //分页
            sql.append("    LIMIT "+(page-1)*count +"," + count);
            Query query=entityManager.createNativeQuery(sql.toString());
            Map<String,Object> result=(Map<String,Object>) queryCount.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
            int total=Integer.parseInt(result.get("count").toString());
            List list=query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
            Object resultAll=ListToString(list,page,count,total);
        return makeResponse(MKOResponseCode.Success, resultAll, "");
        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }
    }



    /**
     * @program: person manager system
     * @description: 详情模块
     * @author: Yuxz
     * @create: 2019-03-07
     **/
    @GetMapping("info")
    MKOResponse info(@RequestParam Integer UserID) {
        try {
            Optional<UserInfo> userInfo = personRepository.findById(UserID);
            if (!userInfo.isPresent()) {
                return makeResponse(MKOResponseCode.DataNotFound, "找不到数据");
            }
            JSONObject obj = (JSONObject) JSONObject.toJSON(userInfo);
            obj.remove("password");
            return makeSuccessResponse(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @program: person manager system
     * @description: 删除模块
     * @author: Yuxz
     * @create: 2019-03-07
     **/
    @GetMapping("delete")
    MKOResponse delete(@RequestParam Integer UserID) {
        try {
            Optional<UserInfo> result = personRepository.findById(UserID);
            if (!result.isPresent()) {
                return makeResponse(MKOResponseCode.DataNotFound, "找不到数据");
            }

            this.personRepository.delete(result.get());
            return makeSuccessResponse("已删除");

        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }


    }

    /**
     * @program: person manager system
     * @description: 添加模块
     * @author: Yuxz
     * @create: 2019-03-07
     **/
    @PostMapping("add")
    MKOResponse add(@RequestBody UserInfo userInfoData) {
        try {
            //判断手机号码格式
//            if (StringUtils.isEmpty(userInfoData.getTel())){}
            if (userInfoData.getTel() == null || userInfoData.getTel().length() != 11) {
                return makeResponse(MKOResponseCode.DataFormatError, "格式错误或密码为空");
            }
            //验证手机号码是否存在
                UserInfo telResult = personRepository.findByTel(userInfoData.getTel());
                if (telResult != null) {
                    return makeResponse(MKOResponseCode.DataExist, "手机号码已存在");
                }
                if (userInfoData.getPassword() == null || userInfoData.getTel() == null) {
                    return makeResponse(MKOResponseCode.ParamsLack, "缺少[password]或[Tel]参数");
                }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userInfoData.getUserName());
            userInfo.setTel(userInfoData.getTel());
            userInfo.setPassword(userInfoData.getPassword());
            userInfo.setAge(userInfoData.getAge());
            userInfo.setSex(userInfoData.getSex() == null ? 0 : userInfoData.getSex());
            userInfo.setRole(userInfoData.getRole() == null ? 0 : userInfoData.getRole());
            userInfo.setState(userInfoData.getState()==null ? 1 : userInfoData.getState());
            userInfo.setUgmtCreate(new Date());
            personRepository.saveAndFlush(userInfo);
            return makeSuccessResponse("已添加");
        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @program: person manager system
     * @description: 修改模块
     * @author: Yuxz
     * @create: 2019-03-07
     **/
    @PostMapping("update")
    MKOResponse update(@RequestBody UserInfo userInfoData) {
        try {

            if (userInfoData.getUserID() == null || userInfoData.getUserID() <= 0) {
                return makeParamsLackResponse("缺少参数或[id]小于等于0");
            }
            //判断ID
            UserInfo updateResult = personRepository.chooseID(userInfoData.getUserID());
            if (updateResult == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "找不到该数据");
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setUserID(userInfoData.getUserID());
            userInfo.setTel(updateResult.getTel());
            userInfo.setUserName(userInfoData.getUserName());
            userInfo.setPassword(userInfoData.getPassword());
            userInfo.setAge(userInfoData.getAge());
            userInfo.setSex(userInfoData.getSex());
            userInfo.setRole(userInfoData.getRole() );
            userInfo.setState(userInfoData.getState());
            userInfo.setUgmtModifeid(new Date());
            personRepository.saveAndFlush(userInfo);
            return makeSuccessResponse("已修改");
        } catch (Exception e) {
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }

    }

    /**
     * @program: person manager system
     * @description: 转换状态模块
     * @author: Yuxz
     * @create: 2019-03-09
     **/
    @GetMapping("switch")
    MKOResponse swich(@RequestParam Integer userID,
                      @RequestParam Integer state){
        try{
            Optional<UserInfo> userInfo=personRepository.findById(userID);
            if(!userInfo.isPresent()){
                return makeResponse(MKOResponseCode.DataNotFound,"找不到此ID");
            }
            userInfo.get().setState(state);
            personRepository.saveAndFlush(userInfo.get());
            return makeResponse(MKOResponseCode.Success,"转换状态成功");
        }catch(Exception e){
            e.printStackTrace();
            return makeBussessErrorResponse("未知错误");
        }
    }
    /**
     * @program: person manager system
     * @description: 修改密码模块
     * @author: Yuxz
     * @create: 2019-03-09
     **/
    @GetMapping("changePassword")
    MKOResponse changePassword (@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                @RequestParam Integer userID){
        try {
            Optional<UserInfo> userResult = personRepository.findById(userID);
            if (!userResult.isPresent()){
                return makeResponse(MKOResponseCode.DataNotFound, "", "找不到数据！");
            }
            if (!oldPassword.equals(userResult.get().getPassword())){
                return makeResponse(MKOResponseCode.UnknownError, "", "密码错误！");
            }
            personRepository.updatePassword(newPassword,userID);
            return  makeSuccessResponse("");

        }catch (Exception e){
            e.printStackTrace();
            return makeBussessErrorResponse("其他错误，修改失败");
        }
    }

    /*
    * 列表数据
    * */
    public Object ListToString(List list,int page,int count ,int countNumber){
        Map<String,Object> map=new HashMap<String, Object>();
        //当前页数
        map.put("page",page);
        //总页数
        if ( count==0 ){
            map.put("pageCount",count);
        }else {
            map.put("pageCount",(countNumber-1)/count + 1);
        }
        //每页条数
        map.put("count",count);
        //总数
        map.put("countNumber",countNumber);
        //数据
        map.put("datas",list);
        return  map;
    }

    //Md5验证 Token方法
    public String refreshToken(UserInfo userInfo){
        String token = TokenUtils.getToken(userInfo.getTel());
        String userTokenKey = formatUserTokenKey(userInfo.getTel());
        String lastToken = redisTemplate.opsForValue().get(userTokenKey);
        if (StringUtils.isNotEmpty(lastToken)) {
            redisTemplate.delete(this.formatTokenKey(lastToken));
        }
        redisTemplate.opsForValue().set(userTokenKey, token);
        redisTemplate.opsForValue().set(this.formatTokenKey(token), JSONObject.toJSONString(userInfo));
        return token;
    }


   private String formatTokenKey(String token) {

        return String.format("%s", token);
    }
    private String formatUserTokenKey(String loginName) {
        String md5 = MD5Util.getMD5(loginName);
        return String.format("%s",md5);
    }



}
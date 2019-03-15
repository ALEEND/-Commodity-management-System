package com.mko.cms.controller;

import com.mko.cms.repository.OdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yuxz
 * @date 2019-03-15 11:54
 */
@RestController
@RequestMapping({"oders"})
public class Orders extends  BaseController {
    @Autowired
    private OdersRepository odersRepository;



}

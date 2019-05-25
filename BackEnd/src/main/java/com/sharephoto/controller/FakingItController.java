package com.sharephoto.controller;

import com.sharephoto.service.GenerateFakeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Joher
 * @data 2019/5/24
 **/
@RestController
public class FakingItController {

    @Autowired
    GenerateFakeData generateFakeData;

    @GetMapping("I/am/gonna/wear/designer/and/forget/your/name")
    public List<String> fakeingIt(){
        return generateFakeData.fakingIt();
    }
}

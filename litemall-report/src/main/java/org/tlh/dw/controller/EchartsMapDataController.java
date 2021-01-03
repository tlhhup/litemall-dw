package org.tlh.dw.controller;

import ch.qos.logback.core.util.CloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.util.ResponseUtil;
import org.tlh.dw.util.UnicodeReader;

import java.io.*;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-03
 */
@Slf4j
@RestController
@RequestMapping("/map")
public class EchartsMapDataController {

    @GetMapping("/json/{id}")
    public Object json(@PathVariable(name = "id") String id) {
        BufferedReader reader=null;
        try {
            File file = ResourceUtils.getFile(String.format("classpath:map/%s.json", id));
            FileInputStream fis = new FileInputStream(file);
            UnicodeReader ur = new UnicodeReader(fis);
            reader = new BufferedReader(ur);
            return ResponseUtil.ok(reader.readLine());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (reader!=null){
                CloseUtil.closeQuietly(reader);
            }
        }
        return ResponseUtil.fail();
    }

}

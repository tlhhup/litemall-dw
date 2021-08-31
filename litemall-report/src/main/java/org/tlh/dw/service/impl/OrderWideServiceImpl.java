package org.tlh.dw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.dw.entity.ck.OrderWide;
import org.tlh.dw.mapper.ck.OrderWideMapper;
import org.tlh.dw.service.OrderWideService;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-08-31
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class OrderWideServiceImpl extends ServiceImpl<OrderWideMapper, OrderWide> implements OrderWideService {
}

package com.chan.backtestBinance.controller;


import com.chan.backtestBinance.data.MarketInput;
import com.chan.backtestBinance.data.MarketOutput;
import com.chan.backtestBinance.service.BackTestService;
import com.chan.backtestBinance.service.ExchangeService;
import com.chan.backtestBinance.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class BackTestController {

    private final BackTestService backTestService;
    private final ExchangeService exchangeService;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {

        MarketOutput test = backTestService.findById(1L);


        return String.valueOf(test.getTotal());
    }

    @RequestMapping("/binanceTest")
    @ResponseBody
    public String binanceTest() throws InterruptedException {
        exchangeService.getOHLCVData("BTCUSDT", "1h", DateUtils.getKoreanStartTime(2020,1,1,0,0,0), DateUtils.getKoreanStartTime(2021,1,1,0,0,0));
        return "Binance Test";
    }

}

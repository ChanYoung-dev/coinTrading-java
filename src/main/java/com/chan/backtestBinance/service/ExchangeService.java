package com.chan.backtestBinance.service;


import com.chan.backtestBinance.data.OHLCVData;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;

public interface ExchangeService {

    CopyOnWriteArrayList<OHLCVData> getOHLCVData(String symbol, String timeFrame, long startTime, long closeTime) throws InterruptedException;
}

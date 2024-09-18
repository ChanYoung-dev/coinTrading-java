package com.chan.backtestBinance.service;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.chan.backtestBinance.data.OHLCVData;
import com.chan.backtestBinance.data.OHLCVId;
import com.chan.backtestBinance.repositoryIMpl.OHLCVRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.chan.backtestBinance.util.DateUtils.convertToKoreanTime;
import static com.chan.backtestBinance.util.DateUtils.getKoreanStartTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class BinanceService implements ExchangeService {

    @Value("${binance.api.key}")
    private String apiKey;

    @Value("${binance.api.secret}")
    private String secretKey;

    @Value("${binance.api.max-weight}")
    private int MAX_WEIGHT_PER_MINUTE;

    // Binance Futures Client
    UMFuturesClientImpl client = new UMFuturesClientImpl(apiKey, secretKey);

    private final OHLCVRepository ohlcvRepository;



    // OHLCV 데이터를 가져오는 메소드
    public CopyOnWriteArrayList<OHLCVData> getOHLCVData(String symbol, String timeFrame, long startTime, long closeTime) throws InterruptedException {
        System.out.println("MAX_WEIGHT_PER_MINUTE = " + MAX_WEIGHT_PER_MINUTE);

        final int WEIGHT_PER_REQUEST = 10; // 각 요청당 weight
        final int MAX_REQUESTS_PER_MINUTE = MAX_WEIGHT_PER_MINUTE / WEIGHT_PER_REQUEST; // 분당 요청 제한 (최대 120회)

        int requestCount = 0; // 분당 요청 카운트

        // closeTime이 0이라면 현재 시간으로 설정
        if (closeTime == 0) {
            LocalDateTime now = LocalDateTime.now();
            closeTime = getKoreanStartTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        }

        // 요청 날짜 출력
        log.info("요청한 데이터: {} ~ {}", convertToKoreanTime(startTime), convertToKoreanTime(closeTime));


        CopyOnWriteArrayList<OHLCVData> allData = new CopyOnWriteArrayList<>();

        // 1. DB에서 startTime ~ closeTime 범위 데이터를 먼저 가져옴
        List<OHLCVData> dbData = ohlcvRepository.findAllByIdSymbolAndIdTimeFrameAndIdOpenTimeBetween(symbol, timeFrame, startTime, closeTime);
        if (!dbData.isEmpty()) {
            allData.addAll(dbData);
        }

        long dbEarliestTime = dbData.isEmpty() ? closeTime : dbData.get(0).getId().getOpenTime();
        long dbLatestTime = dbData.isEmpty() ? startTime : dbData.get(dbData.size() - 1).getId().getOpenTime();

        if (dbData.isEmpty()) {
            log.info("요청하신 데이터가 없습니다({} ~ {} 데이터({}-{}))", convertToKoreanTime(startTime), convertToKoreanTime(closeTime), symbol, timeFrame);
        } else {
            log.info("DB에 저장된 기간: {} ~ {}", convertToKoreanTime(dbEarliestTime), convertToKoreanTime(dbLatestTime));
        }

        // 2. startTime ~ DB의 가장 이른 데이터 사이에 누락된 데이터를 가져오기
        if (startTime < dbEarliestTime) {
            long currentStartTime = startTime;

            while (currentStartTime < dbEarliestTime) {
                // 분당 요청이 최대치를 넘었을 경우 1분 대기
                if (requestCount >= MAX_REQUESTS_PER_MINUTE) {
                    System.out.println("currentStartTime = " + currentStartTime);
                    Thread.sleep(60000); // 1분 대기
                    requestCount = 0; // 요청 카운트 리셋
                }

                CopyOnWriteArrayList<OHLCVData> batchData = fetchBatch(symbol, timeFrame, currentStartTime);
                if (batchData.isEmpty()) {
                    break;
                }
                allData.addAll(batchData);
                currentStartTime = batchData.get(batchData.size() - 1).getId().getOpenTime() + 1;

                // DB에 저장
                for (OHLCVData data : batchData) {
                    ohlcvRepository.save(data);
                }

                log.info("~ {}까지 DB Save.", convertToKoreanTime(batchData.get(batchData.size() - 1).getId().getOpenTime()));

                requestCount++;
            }
        }

        // 3. DB의 마지막 데이터 이후 ~ closeTime까지 누락된 데이터를 가져오기
        if (closeTime > dbLatestTime) {
            long currentStartTime = dbLatestTime + 1;

            while (currentStartTime < closeTime) {
                // 분당 요청이 최대치를 넘었을 경우 1분 대기
                if (requestCount >= MAX_REQUESTS_PER_MINUTE) {
                    System.out.println("currentStartTime = " + currentStartTime);
                    Thread.sleep(60000); // 1분 대기
                    requestCount = 0; // 요청 카운트 리셋
                }

                CopyOnWriteArrayList<OHLCVData> batchData = fetchBatch(symbol, timeFrame, currentStartTime);
                if (batchData.isEmpty()) {
                    break;
                }
                allData.addAll(batchData);
                currentStartTime = batchData.get(batchData.size() - 1).getId().getOpenTime() + 1;

                // DB에 저장
                for (OHLCVData data : batchData) {
                    ohlcvRepository.save(data);
                }

                log.info("~ {}까지 DB Save.", convertToKoreanTime(batchData.get(batchData.size() - 1).getId().getOpenTime()));

                requestCount++;
            }
        }

        return allData;
    }

    // Binance API에서 데이터 가져오기 (1회 요청)
    private CopyOnWriteArrayList<OHLCVData> fetchBatch(String symbol, String timeFrame, long startTime) {
        CopyOnWriteArrayList<OHLCVData> batchData = new CopyOnWriteArrayList<>();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("interval", timeFrame);
        parameters.put("startTime", startTime);
        parameters.put("limit", 1500);  // 최대 1000개의 캔들 요청

        try {
            String result = client.market().klines(parameters);
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray candle = jsonArray.getJSONArray(i);
                OHLCVData data = new OHLCVData(
                        new OHLCVId(symbol, candle.getLong(0), timeFrame),
                        candle.getDouble(1),  // Open
                        candle.getDouble(2),  // High
                        candle.getDouble(3),  // Low
                        candle.getDouble(4),  // Close
                        candle.getDouble(5)   // Volume
                );

                batchData.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return batchData;
    }












}

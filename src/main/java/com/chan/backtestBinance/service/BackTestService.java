package com.chan.backtestBinance.service;

import com.chan.backtestBinance.data.MarketOutput;
import com.chan.backtestBinance.repository.BackTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BackTestService {


    private final BackTestRepository backTestRepository;

    public MarketOutput findById(Long inputId) {
        return backTestRepository.findById(inputId);
    }



}

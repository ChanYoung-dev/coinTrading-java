package com.chan.backtestBinance.repository;

import com.chan.backtestBinance.data.MarketOutput;
import com.chan.backtestBinance.repositoryIMpl.MarketInputRepository;
import com.chan.backtestBinance.repositoryIMpl.MarketOutputRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BackTestRepository {



    private final MarketInputRepository marketInputRepository;
    private final MarketOutputRepository marketOutputRepository;


    public MarketOutput findById(Long inputId) {
        return marketOutputRepository.findById(inputId).orElse(null);
    }
}

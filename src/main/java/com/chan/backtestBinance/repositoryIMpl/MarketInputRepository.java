package com.chan.backtestBinance.repositoryIMpl;

import com.chan.backtestBinance.data.MarketInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketInputRepository extends JpaRepository<MarketInput, Long> {


    Optional<MarketInput> findById(Long inputId);
}

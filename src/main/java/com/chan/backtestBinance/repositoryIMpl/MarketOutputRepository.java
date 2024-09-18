package com.chan.backtestBinance.repositoryIMpl;

import com.chan.backtestBinance.data.MarketOutput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketOutputRepository extends JpaRepository<MarketOutput, Long> {


    Optional<MarketOutput> findById(Long inputId);
}

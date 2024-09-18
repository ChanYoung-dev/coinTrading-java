package com.chan.backtestBinance.repositoryIMpl;

import com.chan.backtestBinance.data.OHLCVData;
import com.chan.backtestBinance.data.OHLCVId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OHLCVRepository extends JpaRepository<OHLCVData, OHLCVId> {

    List<OHLCVData> findAllByIdSymbolAndIdTimeFrameAndIdOpenTimeBetween(String symbol, String timeFrame, long startTime, long closeTime);
}
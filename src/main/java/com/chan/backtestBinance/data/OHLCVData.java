package com.chan.backtestBinance.data;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ohlcv_data")
public class OHLCVData {

    @EmbeddedId
    private OHLCVId id;

    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double volume;
}

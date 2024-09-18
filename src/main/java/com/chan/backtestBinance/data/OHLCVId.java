package com.chan.backtestBinance.data;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OHLCVId implements Serializable {
    private String symbol;
    private long openTime;
    private String timeFrame;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OHLCVId ohlcvId = (OHLCVId) o;
        return openTime == ohlcvId.openTime && Objects.equals(symbol, ohlcvId.symbol) && Objects.equals(timeFrame, ohlcvId.timeFrame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, openTime, timeFrame);
    }
}

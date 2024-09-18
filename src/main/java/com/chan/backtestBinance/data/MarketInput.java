package com.chan.backtestBinance.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Table(name = "market_input")
@Getter
@Entity
public class MarketInput {

    @Id
    private Integer id;

    private String symbol;
    private String timeFrame;
    private Integer volPower;
}

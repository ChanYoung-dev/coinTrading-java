package com.chan.backtestBinance.data;


import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "market_output")
@Getter
@Entity
public class MarketOutput {

    @Id
    private Integer id;

    private Integer total;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private MarketInput marketInput;



}

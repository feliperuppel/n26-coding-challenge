package com.n26.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public class Statistics{

    @JsonFormat(shape=STRING)
    private BigDecimal sum;
    @JsonFormat(shape=STRING)
    private BigDecimal avg;
    @JsonFormat(shape=STRING)
    private BigDecimal max;
    @JsonFormat(shape=STRING)
    private BigDecimal min;
    private int count;

    public Statistics(){
        reset();
    }

    public void reset(){
        sum = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        avg = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        max = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        min = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        count = 0;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

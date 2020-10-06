package org.bitmap.comnhalam.form;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrintReportForm {
    int id;
    String date;
    int number_order;
    Double money;
    Double total;
    Double percent;

    public PrintReportForm() {
        number_order = 0;
        money = 0.0;
        total = 0.0;
        percent = 0.15;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumber_order() {
        return number_order;
    }

    public void setNumber_order(int number_order) {
        this.number_order = number_order;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}

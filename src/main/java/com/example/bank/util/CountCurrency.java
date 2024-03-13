package com.example.bank.util;

import com.example.bank.entity.enums.MoneyType;
import org.springframework.stereotype.Component;

@Component
public class CountCurrency {

    public double countCurrency(double size, MoneyType moneyType, MoneyType cardMoneyType) {
        double value = 0;
        if (cardMoneyType.equals(moneyType)) {
            return size;
        } else {
            switch (moneyType) {
                case AMD:
                    switch (cardMoneyType) {
                        case RUB:
                            value = size / 5;
                            break;
                        case USD:
                            value = size / 400;
                            break;
                    }
                    break;
                case USD:
                    switch (cardMoneyType) {
                        case RUB:
                            value = size * 90;
                            break;
                        case AMD:
                            value = size * 400;
                            break;
                    }
                    break;
                case RUB:
                    switch (cardMoneyType) {
                        case USD:
                            value = size / 0.011;
                            break;
                        case AMD:
                            value = size * 5;
                            break;
                    }
                    break;
            }
        }
        return value;
    }
}

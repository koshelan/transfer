package ru.hm.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TransferApplication {
    public static final float PERCENT_OF_COMMISSION = 0.01f;

    public static void main(String[] args) {
        SpringApplication.run(TransferApplication.class, args);
    }

}

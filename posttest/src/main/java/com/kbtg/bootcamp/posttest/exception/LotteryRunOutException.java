package com.kbtg.bootcamp.posttest.exception;

public class LotteryRunOutException extends RuntimeException{
    public LotteryRunOutException(String message){
        super(message);
    }
}

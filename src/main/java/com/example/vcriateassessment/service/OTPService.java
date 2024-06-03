package com.example.vcriateassessment.service;

import java.util.concurrent.ThreadLocalRandom;

public class OTPService {
    public static String generateOtp() {
        int min = 10000;
        int max = 99999;
        int otp = ThreadLocalRandom.current().nextInt(min, max + 1);
        return String.valueOf(otp);
    }
}
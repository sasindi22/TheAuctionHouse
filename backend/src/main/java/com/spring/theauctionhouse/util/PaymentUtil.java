package com.spring.theauctionhouse.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class PaymentUtil {

    public static String generateHash(String merchantId, String orderId, double amount, String currency, String merchantSecret) {
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String secretHash = getMd5(merchantSecret).toUpperCase();
        String mainString = merchantId + orderId + amountFormatted + currency + secretHash;
        return getMd5(mainString).toUpperCase();
    }

    private static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
package com.assignment.backend.utils;

import java.util.Set;

import com.assignment.backend.data.entities.ProductRate;

public class Utils {

    public static final String JWT_SECRET = "Rookie_Batch_5";
    public static final int JWT_EXP = 840000;

    public static final Boolean PRODUCT_TRADING = true;
    public static final Boolean PRODUCT_STOP_TRADE = false;

    public static final String CATEGORY_ACTIVE = "Active";
    public static final String CATEGORY_DEACTIVATE = "DEACTIVATE";
    public static final String PRODUCT_NOT_FOUND = "Product Not Found";
    public static final String NO_PRODUCT = "No Products Were Found";

    public static final String NO_ACCOUNT = "No Account Were Found";
    public static final String ACC_UPDATE = "Account updated successfully";
    public static final String ACC_DELETE = "Change status of account to FALSE.";
    public static final String ACCOUNT_BLOCK = "Account Blocked.";
    public static final String ACCOUNT_NOT_FOUND = "Account not found, Wrong Username";
    public static final String WRONG_PASS = "Authentication fail: Wrong password";
    public static final String USERNAME_EXITS = "Username already exists";
    public static final String ROLE_NOT_FOUND = "Role is not found, Unable to grant permission";

    public static double rate(Set<ProductRate> listRate) {
        double result = 0.0;
        if (listRate.isEmpty()) {
            return 1.0;
        }

        // calculate average of rate
        result = 5.0;
        return result;
    }

}

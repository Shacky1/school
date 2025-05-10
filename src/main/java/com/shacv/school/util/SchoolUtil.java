package com.shacv.school.util;

import lombok.Builder;
import java.time.Year;

public class SchoolUtil {

    public static String registrationNumberGenerator() {
        /**
         * 2024+randomSixDigits
         */
        Year currentYear = Year.now();

        int min = 10000;
        int max = 99999;

        //generate a random number between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        //convert the current year and random number to strings and concatenate them together
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        StringBuilder registrationNumber = new StringBuilder();

        return registrationNumber.append("S").append(year).append("-").append(randomNumber).toString();
    }

    public static String teacherIdGenerator() {
        /**
         * 2024+randomFourDigits
         */
        int min = 1000;
        int max = 9999;

        //generate a random number between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String randomNumber = String.valueOf(randNumber);

        StringBuilder teacherId = new StringBuilder();

        return teacherId.append("ST").append(randomNumber).toString();
    }

    // Method to calculate the year of study
    public static int calculateYearOfStudy(int enrollmentYear) {
        int currentYear = Year.now().getValue();
        return currentYear - enrollmentYear + 1;
    }
}

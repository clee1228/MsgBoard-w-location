package com.example.cs160_sp18.prog3;

import java.util.Comparator;

public class LandmarkComparator implements Comparator<Landmarks>{

    @Override
    public int compare(Landmarks a, Landmarks b) {
        String lhs = a.getDist();
        String rhs = b.getDist();
        boolean lhsStartsWithLetter = Character.isLetter(lhs.charAt(0));
        boolean rhsStartsWithLetter = Character.isLetter(rhs.charAt(0));
        if (lhsStartsWithLetter && rhsStartsWithLetter) {
            // they both start with letters or not-a-letters
            return lhs.compareTo(lhs);
        }else if(!lhsStartsWithLetter && !rhsStartsWithLetter){


            String l = lhs.substring(0, lhs.indexOf(' '));
            String r = rhs.substring(0, rhs.indexOf(' '));
            int left = Integer.valueOf(l);
            int right = Integer.valueOf(r);

            if (left > right){
                return 1;
            }else{
                return -1;
            }

        } else if (lhsStartsWithLetter) {
            // the first string starts with letter and the second one is not
            return -1;
        } else {
            // the second string starts with letter and the first one is not
            return 1;
        }
    }

}

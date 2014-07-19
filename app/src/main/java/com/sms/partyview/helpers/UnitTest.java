package com.sms.partyview.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sque on 7/18/14.
 */
public class UnitTest {
    // For testing values.
    private static void expectEqual(int i1, int i2) {
        if (i1 != i2) {
            System.err.println("value [" + i1 + "] does not match [" + i2 + "]");
        }
    }
    private static void expectEqual(String s1, String s2) {
        if (!s1.equals(s2)) {
            System.err.println("String [" + s1 + "] does not match [" + s2 + "]");
        }
    }

    public static void testStringSplit() {
        // Various input strings to test.
        List<String> inputs = new ArrayList<String>();
        inputs.add("Simon,Sandra,My");
        inputs.add("Simon, Sandra, My");
        inputs.add(" Simon, Sandra, My ");
        inputs.add(" Simon ,  Sandra ,  My ");
        inputs.add("Simon, Sandra, My, ");
        inputs.add("Simon, Sandra, My,");
        inputs.add(", Simon, Sandra, My,");
        inputs.add(" , Simon, Sandra, My, ");

        // The above strings should all split into the following set of strings.
        List<String> expected = new ArrayList<String>();
        expected.add("Simon");
        expected.add("Sandra");
        expected.add("My");

        for (String input : inputs) {
            List<String> output = Utils.splitString(input);
            System.out.println("Testing input: [" + input + "]");
            expectEqual(output.size(), 3);
            for (int i = 0; i < output.size() && i < expected.size(); ++i) {
                expectEqual(expected.get(i), output.get(i));
            }
        }
    }
}

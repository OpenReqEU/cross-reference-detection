import com.essi.dependency.functionalities.Grammar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriverRegex {

    public static void main(String[] args) {
        Grammar grammar = new Grammar(null);
        /*String text_simple_1 = " section-1  subsections:1.1 paragraph-Z volume=iv  book#ix qtbug-(1324) autosuite-(A.341+.)";
        test_one("Simple_1",grammar.getSimpleExp_1(),text_simple_1);
        String text_simple_2_1 = "1st item";
        String text_simple_2_2 = "2nd subsect.";
        String text_simple_2_3 = "3rd lot";
        String text_simple_2_4 = "4th sect.";
        test_one("Simple_2",grammar.getSimpleExp_2(),text_simple_2_1);
        test_one("Simple_2",grammar.getSimpleExp_2(),text_simple_2_2);
        test_one("Simple_2",grammar.getSimpleExp_2(),text_simple_2_3);
        test_one("Simple_2",grammar.getSimpleExp_2(),text_simple_2_4);
        String text_simple_3 = "above section-1 current paragraph-A next book#ix";
        test_one("Simple_3",grammar.getSimpleExp_3(),text_simple_3);
        String text_simple_4 = "previous 1st lot";
        test_one("Simple_4",grammar.getSimpleExp_4(),text_simple_4);
        String text_simple_5 = " section-1 preceding";
        test_one("Simple_5",grammar.getSimpleExp_5(),text_simple_5);
        String text_simple_6 = "that follows section";
        test_one("Simple_6",grammar.getSimpleExp_6(),text_simple_6);
        String text_simple_7 = " lot preceding";
        test_one("Simple_7",grammar.getSimpleExp_7(),text_simple_7);*/

        /*String text_complex_1 = " lot-213 to 313 , subitem#xi or iv, subparagraph:21312 or 3213, section-3 to 1";
        test_one("Complex",grammar.getComplexExp_1(),text_complex_1);
        String text_complex_2 = " 1st article 2nd article or #12132 ";
        test_one("Complex",grammar.getComplexExp_2(),text_complex_2);
        String text_complex_3 = " lot-213 and -313 of above section ";
        test_one("Complex",grammar.getComplexExp_3(),text_complex_3);
        String text_complex_4 = " item#2 to #3 above items";
        test_one("Complex",grammar.getComplexExp_4(),text_complex_4);
        String text_complex_5 = " article:A - article:B of the book-ix";
        test_one("Complex",grammar.getComplexExp_5(),text_complex_5);*/

        /*String text_complex_6 = "";
        test_one("Complex",grammar.getComplexExp_6(),text_complex_6);
        String text_complex_7 = "";
        test_one("Complex",grammar.getComplexExp_7(),text_complex_7);*/

        String text_external_1 = " article 2 of the contract";
        test_one("External",grammar.getExternalTerm_1(),text_external_1);
        String text_external_2 = " item#2 to #3 of a tendering documentation";
        test_one("External",grammar.getExternalTerm_2(),text_external_2);
        String text_external_3 = " 4th article or 1st article and -3 and of the technical specifications";
        test_one("External",grammar.getExternalTerm_3(),text_external_3);
        String text_external_4 = " contractor proposal, described in item#2 to #3";
        test_one("External",grammar.getExternalTerm_4(),text_external_4);
    }

    private static void test_one(String message, String regex, String text) {
        System.out.println("------------------------");
        System.out.println(message);
        //System.out.println(regex);
        Pattern pattern = Pattern.compile("(" + regex + ")");
        Matcher matcher = pattern.matcher(text);
        String result = "";
        while (matcher.find()) {
            result += matcher.group(1).replaceAll("\\(|\\)", "") + System.lineSeparator();
        }
        System.out.println(result);
    }
}

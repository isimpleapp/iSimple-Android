package com.treelev.isimple.utils;

import java.util.HashMap;

public class Cyr2AsciiConverter {

    private static final HashMap<Character, String> characterMap;
    private static final HashMap<String, Character> inverseCharacterMap;

    static {
        characterMap = new HashMap<Character, String>();
        characterMap.put('а', "#a"); characterMap.put('А', "#A");
        characterMap.put('б', "#b"); characterMap.put('Б', "#B");
        characterMap.put('в', "#v"); characterMap.put('В', "#V");
        characterMap.put('г', "#g"); characterMap.put('Г', "#G");
        characterMap.put('д', "#d"); characterMap.put('Д', "#D");
        characterMap.put('е', "#e"); characterMap.put('Е', "#E");
        characterMap.put('ё', "#^`"); characterMap.put('Ё', "#^~");
        characterMap.put('ж', "#j"); characterMap.put('Ж', "#J");
        characterMap.put('з', "#z"); characterMap.put('З', "#Z");
        characterMap.put('и', "#i"); characterMap.put('И', "#I");
        characterMap.put('й', "#^q"); characterMap.put('Й', "#^Q");
        characterMap.put('к', "#k"); characterMap.put('К', "#K");
        characterMap.put('л', "#l"); characterMap.put('Л', "#L");
        characterMap.put('м', "#m"); characterMap.put('М', "#M");
        characterMap.put('н', "#n"); characterMap.put('Н', "#N");
        characterMap.put('о', "#o"); characterMap.put('О', "#O");
        characterMap.put('п', "#p"); characterMap.put('П', "#P");
        characterMap.put('р', "#r"); characterMap.put('Р', "#R");
        characterMap.put('с', "#s"); characterMap.put('С', "#S");
        characterMap.put('т', "#t"); characterMap.put('Т', "#T");
        characterMap.put('у', "#u"); characterMap.put('У', "#U");
        characterMap.put('ф', "#f"); characterMap.put('Ф', "#F");
        characterMap.put('х', "#h"); characterMap.put('Х', "#H");
        characterMap.put('ц', "#c"); characterMap.put('Ц', "#C");
        characterMap.put('ч', "#^x"); characterMap.put('Ч', "#^X");
        characterMap.put('ш', "#^i"); characterMap.put('Ш', "#^I");
        characterMap.put('щ', "#^o"); characterMap.put('Щ', "#^O");
        characterMap.put('ъ', "#^]"); characterMap.put('Ъ', "#^}");
        characterMap.put('ы', "#^s"); characterMap.put('Ы', "#^S");
        characterMap.put('ь', "#^m"); characterMap.put('Ь', "#^M");
        characterMap.put('э', "#^'"); characterMap.put('Э', "#^\"");
        characterMap.put('ю', "#^."); characterMap.put('Ю', "#^>");
        characterMap.put('я', "#^z"); characterMap.put('Я', "#^Z");

        inverseCharacterMap = new HashMap<String, Character>(characterMap.size());
        for(char c : characterMap.keySet()) {
            inverseCharacterMap.put(characterMap.get(c), c);
        }
    };

    public static String convertToAscii(String text) {
        StringBuffer result = new StringBuffer(text.length());
        for (char c : text.toCharArray()) {
            String s = characterMap.get(c);
            result.append(s != null ? s : c);
        }
        return result.toString();
    }


    public static String convertToCyrillic(String text) {
        char[] textChars = text.toCharArray();
        StringBuffer result = new StringBuffer(text.length());

        boolean state1 = false;
        boolean state2 = false;

        for (int i = 0; i < textChars.length; i++) {
            Character c = textChars[i];
            if (c == '#') {
                if (state1) {
                    result.append(c);
                }
                state1 = true;
                state2 = false;
            }
            else if (c == '^' && state1) {
                state2 = true;
            }
            else if (state1) {
                String s = (state2 ? "#^" : "#") + c;
                c = inverseCharacterMap.get(s);
                result.append(c != null ? c : s);
                state1 = false;
                state2 = false;
            }
            else {
                result.append(c);
            }
        }
        return result.toString();
    }
}

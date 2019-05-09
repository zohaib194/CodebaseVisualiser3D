package me.codvis.ast;

import java.util.ArrayList;
import java.util.List;

public class ParserTestCase {
    public String listenername;
    public String filename;
    public String input;
    public List<String> expected = new ArrayList<>();
    public List<String> illegal = new ArrayList<>();
    public String errMsg;

    public ParserTestCase(String listenername, String filename, String input) {
        this.listenername = listenername;
        this.filename = filename;
        this.input = input;
    }
}
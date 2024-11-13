package org.ulpgc.implementations;

import org.ulpgc.ports.Input;

import java.util.Scanner;

public class SearchInput implements Input {
    private static final Scanner scanner = new Scanner(System.in);
    @Override
    public String input() {
        //Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter the word/words you want to search for: ");
        return scanner.nextLine().trim();
    }
}

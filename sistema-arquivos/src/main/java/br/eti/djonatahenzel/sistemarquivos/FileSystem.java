package br.eti.djonatahenzel.sistemarquivos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileSystem {

    // FIXME ajustar para o caminho no PC do aluno
    public static final String ROOT = "C:" + File.separator + "utfpr" + File.separator + "hd";

    public FileSystem() {
        executar();
    }

    private void executar() {

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Bem vindo ao sistema de arquivos!");

        var stop = false;
        var currentPath = Paths.get(ROOT);

        while (!stop) {
            try {
                System.out.print("$> ");
                final var command = Command.parseCommand(scanner.nextLine());
                currentPath = command.execute(currentPath);
                stop = command.shouldStop();
            } catch (UnsupportedOperationException | IOException ex) {
                System.out.printf("%s", ex.getMessage()).println();
            }
        }

        System.out.println("Sistema de arquivos encerrado.");
    }

    public static void main(String[] args) {
        new FileSystem();
    }
}

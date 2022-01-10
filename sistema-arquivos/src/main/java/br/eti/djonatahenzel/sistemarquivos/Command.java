package br.eti.djonatahenzel.sistemarquivos;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {
           listarConteudo(path);
            System.out.println("Caminho atual: " + path);
            return path;
        }

    },
    SHOW() {
        private static String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) {
            // if verifica se o usuario passou o nome de um arquivo por parametro, caso não tenha passado dispara a exception
            if (this.parameters.length != 1 && !this.parameters[1].contains(".")) {
                br.eti.djonatahenzel.sistemarquivos.FileReader fileReader = new br.eti.djonatahenzel.sistemarquivos.FileReader();
                fileReader.read(path, this.parameters[1]);      //pega o que foi passado por parametro e le o arquivo
                System.out.println("Caminho atual: " + path);
                return path;
            } else {
                throw new UnsupportedOperationException("Informe o nome do arquivo corretamente --- Caso esteja correto formato não suportado!!");
            }
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {
            if(FileSystem.ROOT.equals(path.toString())){
                System.out.println("Caminho atual: " + path);
                throw new UnsupportedOperationException("Não tem como voltar mais. O caminho atual já é na pasta raiz");
            }else {
                String caminhoDiretorio [] = FileSystem.ROOT.split("/");
                String voltarUmDiretorio = caminhoDiretorio[caminhoDiretorio.length - 1].toString();
                System.out.println("Caminho atual: " + path);
                System.out.println(voltarUmDiretorio);
                return path.getParent();
            }
        }
    },

    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {
            Path caminhoSelecionado = null;
            File diretorio = new File(path.toString());
            File arquivos[] = diretorio.listFiles();
            if (this.parameters.length != 1) {
                for (int i = 0; i < arquivos.length; i++) {
                    // Ao percorer no vetor ele somente permite abrir pastas. Pois os arquivos são validados pelo formato de extensão .algumaCoisa -
                    if (arquivos[i].getName().equals(this.parameters[1]) && !this.parameters[1].contains(".")) {
                       caminhoSelecionado = path.resolve(this.parameters[1]);
                       System.out.println("Caminho atual: " + caminhoSelecionado);
                       return caminhoSelecionado;
                    }
                }
            }
            if (caminhoSelecionado == null) {
                throw new UnsupportedOperationException("ERRO: Diretorio incorreto. Verifique o diretorio e tente novamente!!");
            } else {
                throw new UnsupportedOperationException("ERRO: Informe o diretorio que deseja abrir");
            }
        }
    },

    DETAIL() {
            private String[] parameters = new String[]{};

            @Override
            void setParameters (String[]parameters){
                this.parameters = parameters;
            }

            @Override
            boolean accept (String command){
                final var commands = command.split(" ");
                return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
            }

            @Override
            Path execute (Path path) throws IOException {
                File posicao = new File(path.toString());
                File arquivos []  = posicao.listFiles();
                Path caminho = null;
                    if (this.parameters.length != 1) {
                        for (int i = 0; i < arquivos.length; i++) {
                            if (arquivos[i].getName().equals(this.parameters[1])) {
                                caminho = path.resolve(this.parameters[1]);
                            }
                        }
                        //Apresentação das informações do arquivo
                        var tela = Files.getFileAttributeView(caminho, BasicFileAttributeView.class);
                        var informacoesAtributos = tela.readAttributes();
                        System.out.println("Dados do arquivo ou pasta: ");
                        System.out.println("----------------------------------------------");
                        System.out.println("Tamanho: " + informacoesAtributos.size() + "Mb");
                        System.out.println("Data criação: " + informacoesAtributos.creationTime());
                        System.out.println("Data de Modificação: " + informacoesAtributos.lastModifiedTime());

                        return caminho;
                    } if (caminho == null) {
                        throw new UnsupportedOperationException("ERRO: Diretorio incorreto. Favor verificar e tentar novamente!!");
                    } else {
                        throw new UnsupportedOperationException("ERRO: Informe o diretorio que deseja abrir!!");
                    }
                }
    },

    EXIT() {
            @Override
            boolean accept (String command){
                final var commands = command.split(" ");
                return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
            }

            @Override
            Path execute (Path path){
                System.out.print("Saindo...");
                return path;
            }

            @Override
            boolean shouldStop () {
                return true;
            }
    };

        abstract Path execute(Path path) throws IOException;

        abstract boolean accept(String command);

        void setParameters(String[] parameters) {
        }

        boolean shouldStop() {
            return false;
        }

        public static Command parseCommand(String commandToParse) {

            if (commandToParse.isBlank()) {
                throw new UnsupportedOperationException("Type something...");
            }

            final var possibleCommands = values();

            for (Command possibleCommand : possibleCommands) {
                if (possibleCommand.accept(commandToParse)) {
                    possibleCommand.setParameters(commandToParse.split(" "));
                    return possibleCommand;
                }
            }

            throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
        }

        // ---------------- Metodos uteis--------------
        private static Path listarConteudo(Path path) {
            File arquivo = new File(path.toString());
            File files []= arquivo.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
            }
            System.out.println("");
            return path;
        }
    }



package br.eti.djonatahenzel.sistemarquivos;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path, String nomeArquivo) {
       int arquivoControl;
       try{
           File arquivo = new File(path.toString(), nomeArquivo);
           java.io.FileReader fileReader = new java.io.FileReader(arquivo);
           while ((arquivoControl = fileReader.read()) != -1){
               System.out.print((char)arquivoControl);
           }
           fileReader.close();
       }   catch (IOException ex){
       throw new UnsupportedOperationException("Arquivo informado não foi encontrado!! Verique e tente novamente");
       }
    }
}

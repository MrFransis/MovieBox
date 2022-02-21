package it.unipi.dii.dmml.moviebox.classification;

import it.unipi.dii.dmml.moviebox.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ClassifierDriver {
    private DataOutputStream dataOutputStream;
    private DataInputStream bufferedReader;
    private final int FILE_PACKET_SIZE = 4 * 1024;
    private final String classifierServerIp;
    private final int classifierServerPort;

    public ClassifierDriver() {
        Properties configurationParameters = Utils.readConfigurationParameters();
        this.classifierServerIp = configurationParameters.getProperty("classifierServerIp");
        this.classifierServerPort = Integer.parseInt(configurationParameters.getProperty("classifierServerPort"));
    }


    /**
     * Function that returns genre given the plot of the film
     * @param plot Plot of to the film
     * @return film genre
     */
    public String getMovieGenre(String plot)
    {
        try(Socket socket = new Socket(classifierServerIp,classifierServerPort)) {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new DataInputStream(socket.getInputStream());

            sendPlot();

            String genre = bufferedReader.readUTF();
            dataOutputStream.close();
            bufferedReader.close();
            return genre;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Function that sends a file to the server, dividing it into packet
     * @throws Exception if something goes wrong
     */
    private void sendPlot() throws Exception{
        int bytes;
        File file = new File(getClass().getResource("/it/unipi/dii/dmml/moviebox/data/plot.txt").toURI().getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        // Send the commands
        dataOutputStream.writeUTF("SendPlot");
        dataOutputStream.flush();

        // send file size
        dataOutputStream.writeUTF(String.valueOf(file.length()));
        dataOutputStream.flush();

        // break file into chunks
        byte[] buffer = new byte[FILE_PACKET_SIZE];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0, bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}
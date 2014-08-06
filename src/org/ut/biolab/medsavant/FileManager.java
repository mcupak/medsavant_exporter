/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant;

import com.dnanexus.DXAPI;
import com.dnanexus.DXFile;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * File manipulation controller in the platform.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public class FileManager {

    /**
     * Obtains File from DXFile.
     *
     * @param file DXFile id (DNAnexus link)
     * @return local file name
     * @throws java.net.MalformedURLException
     * @throws java.io.FileNotFoundException
     */
    public static String downloadDXFile(DXFile file) throws MalformedURLException, FileNotFoundException, IOException {
        String res = file.describe().getName();

        // raw call here, surely there is a better way to do this
        JsonNode fileDownload = DXAPI.fileDownload(file.getId());
        URL website = new URL(fileDownload.get("url").asText());

        URLConnection uc = website.openConnection();

        uc.setRequestProperty("Cookie", fileDownload.get("headers").get("Cookie").asText());
        System.out.println(uc.getHeaderFields().toString());

        ReadableByteChannel rbc = Channels.newChannel(uc.getInputStream());
        FileOutputStream fos = new FileOutputStream(res);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();

        return res;
    }
}

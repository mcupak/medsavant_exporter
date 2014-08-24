/*
 * Copyright (C) 2014 Miroslav Cupak (mirocupak@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
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
        String fileName = file.describe().getName();

        // raw call here, surely there is a better way to do this
        JsonNode fileDownload = DXAPI.fileDownload(file.getId());
        URL url = new URL(fileDownload.get("url").asText());

        URLConnection uc = url.openConnection();
        uc.setDoInput(true);
        uc.setDoOutput(false);
        uc.setRequestProperty("Cookie", fileDownload.get("headers").get("Cookie").asText());

        ReadableByteChannel rbc = Channels.newChannel(uc.getInputStream());
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();

        return fileName;
    }
}

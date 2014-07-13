package com.epam.kozhanbergenov.shop.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ImageController extends HttpServlet {
    private static final Logger log = Logger.getLogger(Controller.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("ImageController was started");
        ServletOutputStream out = resp.getOutputStream();
        byte[] image = null;
        try {
            image = getImage(req.getParameter("pathToImage"));
        } catch (Exception e) {
            log.error(e);
        }
        out.write(image != null ? image : new byte[0]);
    }

    public static byte[] getImage (String imageFileName) throws Exception {
        if (imageFileName == null) {
            return null;
        }
        InputStream inputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            log.debug(imageFileName);
            inputStream = new FileInputStream(new File(imageFileName));

        } catch (FileNotFoundException e) {
            log.error(e);
        }
        int read = 0;
        byte[] bytes = new byte[1024];
           while(( read = inputStream.read(bytes)) > 0 ) {
               out.write(bytes, 0, read);
           }
        byte[] imageData = out.toByteArray();
        return imageData;

    }
}

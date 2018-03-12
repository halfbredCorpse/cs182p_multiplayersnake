/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snakeUI;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Jasmin Rose
 */
public class FontChooser {
    public Font returnFont(String fontName, float size){
    //Adds custom fonts
        try {
            InputStream is = new BufferedInputStream(Files.newInputStream(Paths.get(fontName)));
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            Font sizedFont = font.deriveFont(Font.BOLD,size);
            return sizedFont;
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        catch(FontFormatException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}

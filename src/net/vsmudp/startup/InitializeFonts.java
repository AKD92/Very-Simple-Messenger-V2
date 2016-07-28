package net.vsmudp.startup;

import java.awt.*;
import java.io.*;
import java.util.*;

import net.vsmudp.PropertyName;
import net.vsmudp.Application;

public class InitializeFonts implements Runnable {
	
	private String STR_DLG;
	private Application app;
	
	public InitializeFonts() {
		STR_DLG = "Dialog";
		app = Application.getInstance();
	}
	
	public void run() {
		buildFonts();
		adjustMessageFont();
	}
	
	private void buildFonts() {
		Font f = new Font(STR_DLG, Font.PLAIN, 10);
		app.fontMap.put(PropertyName.FONT_SLIDER_LABEL, f);
		f = new Font(STR_DLG, Font.BOLD, 17);
		app.fontMap.put(PropertyName.FONT_MSG_TYPE_RENDERER, f);
		f = new Font(STR_DLG, Font.PLAIN, 15);
		app.fontMap.put(PropertyName.FONT_TEXT_VIEWER, f);
	}
	
	private void adjustMessageFont() {
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = env.getAvailableFontFamilyNames();
		
		String[] my_fonts = new String[] {"Siyam Rupali", "Arial Unicode MS"};
		
		Font intermediateFont= null;
		
		long current = System.currentTimeMillis();
		
		Arrays.sort(fonts);
		int loc = -1;
		for (String x : my_fonts) {
			loc = Arrays.binarySearch(fonts, x);
			if (loc >= 0) {
				intermediateFont = new Font(fonts[loc], Font.PLAIN, 14);
				break;
			}
		}
		
		if (intermediateFont == null) {
			File fontFile = new File("font.ttf");
			String name = fontFile.getName();
			app.println(String.format("Using custom font from : %s", name));
			try {
				intermediateFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
				String font_name = intermediateFont.getFontName();
				app.println(String.format("Custom font created from %s name: %s", name, font_name));
			} catch (Exception e) {
				intermediateFont = new Font(STR_DLG, Font.PLAIN, 12);
				app.println("Custom font can not be created, using DIALOG font");
				e.printStackTrace();
			}
		}
		
		current = System.currentTimeMillis() - current;
		Font font_Message = intermediateFont;
		app.fontMap.put(PropertyName.FONT_MSG_VIEW_EDITOR, font_Message);
		String msg = String.format("Font %s found in %d milli seconds"
				, intermediateFont.getFontName(),  current);
		app.println(msg);
		
	}

}

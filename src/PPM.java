/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


// Classes used to load/save from files/URL
import java.io.File;
import java.io.IOException;

import java.io.EOFException;
import java.io.FileInputStream;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;

/** Define ppm files load/save */

class ppm {

	public int width = -1;
	public int height = -1;
	private int width_height;
	BufferedImage ppmi;

	@SuppressWarnings({ "deprecation" })
	public ppm load(String file) throws IOException {

		if (file.startsWith("file:"))
			file = file.substring(5);
		
		DataInputStream f = new DataInputStream(new FileInputStream(new File(file)));
		String h = readLine(f);
		
		if (h.equals("P6")) { // PPM PM6
			set(f);
			ppmi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			byte bytes[] = new byte[3 * width_height];
			f.read(bytes);
			for (int i = 0, k = 0; i < width_height; i++) {
				int red = intValue(bytes[k++]);
				int green = intValue(bytes[k++]);
				int blue = intValue(bytes[k++]);
				ppmi.setRGB(i % width, i / width, (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));

			}
			f.close();
		} else if (h.equals("P3")) { // PPM PM3
			set(f);
			ppmi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int reds = 0;
			int greens = 0;
			int blues = 0;

			for (int i = 0; i < width_height * 3; i++) {

				String check = f.readLine();
				if (i % 3 == 0) {

					reds = Integer.parseInt(check);
				}
				if (i % 3 == 1) {

					greens = Integer.parseInt(check);
				}
				if (i % 3 == 2) {

					blues = Integer.parseInt(check);
				}
				ppmi.setRGB((i / 3) % width, i / 3 / width,
						(reds & 0xFF) << 16 | (greens & 0xFF) << 8 | (blues & 0xFF));

				
			}
			f.close();
		} else {
			f.close();
			throw new IOException("Bad file format: not a PPM/PGM raw format");
		}
		return this;
	}

	private void set(DataInputStream f) throws IOException {
		String l;
		while ((l = readLine(f)).startsWith("#"))
			;
		try {
			width = new Integer(l.substring(0, l.indexOf(' '))).intValue();
			height = new Integer(l.substring(l.indexOf(' ') + 1)).intValue();
			width_height = width * height;
			// pixels = new int[width_height];
		} catch (NumberFormatException e) {
			throw new IOException("Bad file sizes format");
		}
		if (!readLine(f).equals("255"))
			throw new IOException("Bad PPM/PGM file format");
	}

	private static int intValue(byte b) {
		return (b < 0) ? (256 + b) : b;
	}

	private static String readLine(DataInputStream f) throws IOException {
		StringBuffer s = new StringBuffer();
		try {
			char c;
			while ((c = (char) f.readByte()) != '\n')
				s.append(c);
		} catch (EOFException e) {
		}
		return s.toString();
	}

}

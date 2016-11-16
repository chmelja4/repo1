package hra;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Hrac {
	public static final int SIRKA = 40;
	public static final int VYSKA = 33;
	//velikost skoku hr·Ëe
	private static final int KOEF_ZRYCHLENI = 1;
	//rychlost padu hrace
	private static final int KOEF_RYCHLOST = 2;
	private BufferedImage img = null;
	//pocatecni x-ova pozice hrace, nemeni se (hrac neskace dopredu, dozadu)
	private int x;
	//pocatecni x-ova pozice hrace, meni se (hrac skace nahoru, dolu)
	private int y;
	private int rychlost;
	
	public Hrac(BufferedImage img) {
		this.img=img;
		x = (HraciPlocha.SIRKA /2) - (img.getWidth() / 2);
		y = HraciPlocha.VYSKA / 2;
		
		rychlost = KOEF_RYCHLOST;
	}
	
	/**
	 * vola se po narazu do zdi, do kraje okna
	 */
	public void reset() {
		y = HraciPlocha.VYSKA / 2;
		rychlost = KOEF_RYCHLOST;
	}
	
	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
	
	public void skok() {
		rychlost = -17;
	}
	/**
	 * zajiöùuje pohyb hr·Ëe
	 */
	public void posun() {
		rychlost = rychlost + KOEF_ZRYCHLENI;
		y = y + rychlost;
	}
	
	public void paint(Graphics g) {
		g.drawImage(img, x, y, null);	
		if (HraciPlocha.DEBUG) {
			g.setColor(Color.WHITE);
			//g.drawString("[x="+x+",y="+y+", rychlost="+rychlost+"]", x, y-5);
		}
	}
	
	public int getVyskaHrace() {
		return img.getHeight();
	}
	
	/**
	 * 
	 * vracÌ pomysln˝ ctverec/obdelnik, kter˝ opisuje hrace
	 */
	public Rectangle getMez() {
		return new Rectangle(x, y, img.getWidth(), img.getHeight());
	}
}

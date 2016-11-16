package hra;

import java.awt.image.BufferedImage;

public class Zed {

	public static final int SIRKA = 45;
	//rychlost pohybu zdi
	public static final int RYCHLOST = -6;
	public static final int MEZERA_MEZI_HORNI_A_DOLNI_CASTI_ZDI = -200;
	
	//ruzne zdi, ruzne obrázky =>nelze použít static
	private static BufferedImage img = null;
	//x-ova souradnice zdi(meni se zprava doleva)
	private int x; 
	//y-ova souradnice zdi(horni souradnice spodni casti zdi)
	private int y;
	private int vyska;
	
	public Zed(int vzdalenostZdiOdZacatkuHraciPlochy) {
		this.x = vzdalenostZdiOdZacatkuHraciPlochy;
		//TODO
	}
	

}

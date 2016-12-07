package hra;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import obrazek.ManazerObrazku;
import obrazek.Obrazek;
import obrazek.ZdrojObrazkuSoubor;

public class HraciPlocha extends JPanel {
	public static final boolean DEBUG = true;
	public static final int VYSKA = 800;
	public static final int SIRKA = 600;

	// rychlost bìhu pozadí
	public static final int RYCHLOST = -2;
	// musi byt alespon tri zdi, jinak se prvni zed nestihne posunout za levy
	// okraj
	// =nestihne zajet za levy okraj hraci plochy drivenez je potreba ji
	// posunout
	// pred pravy okraj hraci plochy a vykreslit
	public static final int POCET_ZDI = 4;

	private SeznamZdi seznamZdi;
	private Zed aktualniZed;
	private Zed predchoziZed;
	private int skore = 0; // kolika zdmi hrac uspesnì prošel bez narazu

	private Hrac hrac;
	private JLabel lbSkore;
	private JLabel lbZprava;
	private Font font;
	private Font fontZpravy;
	private BufferedImage imgPozadi;
	private Timer casovacAnimace;
	private boolean pauza = false;
	private boolean hraBezi = false;
	private int posunPozadiX = 0;

	public HraciPlocha(ManazerObrazku mo) {
		imgPozadi = mo.getObrazek(Obrazek.POZADI);
		hrac  = new Hrac(mo.getObrazek(Obrazek.HRAC));
		Zed.setObrazek(mo.getObrazek(Obrazek.ZED));

		seznamZdi = new SeznamZdi();

		vyrobFontyALabely();

	}

	private void vyrobFontyALabely() {
		font = new Font("Arial", Font.BOLD, 40);
		this.setLayout(new BorderLayout());
		lbZprava = new JLabel("");
		lbZprava.setFont(fontZpravy);
		lbZprava.setForeground(Color.ORANGE);
		lbZprava.setHorizontalAlignment(SwingConstants.CENTER);

		lbSkore = new JLabel("0");
		lbSkore.setFont(font);
		lbSkore.setForeground(Color.ORANGE);
		lbSkore.setHorizontalAlignment(SwingConstants.CENTER);

		this.add(lbSkore, BorderLayout.NORTH);
		this.add(lbZprava, BorderLayout.CENTER);

	}

	private void vyrobZdi(int pocet) {
		Zed zed;
		int vzdalenost = HraciPlocha.SIRKA;

		for (int i = 0; i < pocet; i++) {
			zed = new Zed(vzdalenost);
			seznamZdi.add(zed);
			vzdalenost = vzdalenost + (HraciPlocha.SIRKA / 2);
		}

		vzdalenost = vzdalenost - HraciPlocha.SIRKA - Zed.SIRKA;
		Zed.setVzdalenostPosledniZdi(vzdalenost);

	}

	public void paint(Graphics g) {
		super.paint(g);

		// dve pozadi za sebe pro plynule prechody
		// prvni
		g.drawImage(imgPozadi, posunPozadiX, 0, null);
		// druhe je posunute o sirku obrazku
		g.drawImage(imgPozadi, posunPozadiX + imgPozadi.getWidth(), 0, null);

		if (HraciPlocha.DEBUG) {
			g.setColor(Color.WHITE);
			// g.drawString("posun pozadi X="+posunPozadiX, 0, 10);
		}

		for (Zed zed : seznamZdi) {
			zed.paint(g);
		}

		//
		hrac.paint(g);
		lbSkore.paint(g);
		lbZprava.paint(g);
	}

	private void posun() {
		if (!pauza && hraBezi) {
			// nastav zed v poradi
			aktualniZed = seznamZdi.getAktualniZed();

			// nastav predchozi zed
			predchoziZed = seznamZdi.getPredchoziZed();

			// detekce kolizí
			if (isKolizeSeZdi(predchoziZed, hrac) || isKolizeSeZdi(aktualniZed, hrac)
					|| isKolizeSHraniciHraciPlochy(hrac)) {
				ukonciAVyresetujHruPoNarazu();
			} else {
				for (Zed zed : seznamZdi) {
					zed.posun();

				}

				hrac.posun();

				// hrac prosel zdie bez narazu
				// zjistit kde se nachazi
				// bud pred aktualni zdi - nedelej nic
				// nebo za aktualni posun dalsi zed v poradi
				// a prepocitej skore

				if (hrac.getX() >= aktualniZed.getX()) {
					seznamZdi.nastavDalsiZedNaAktualni();
					zvedniSkoreZed();
					lbSkore.setText(skore + "");
				}

			}

			// posun pozice pozadi hraci plochy(scrollovaní)
			posunPozadiX = posunPozadiX + HraciPlocha.RYCHLOST;
			// kdyz se pozadi cele doposouva, zacni od zacatku
			if (posunPozadiX == -imgPozadi.getWidth()) {
				posunPozadiX = 0;
			}

		}

	}

	private void ukonciAVyresetujHruPoNarazu() {
		hraBezi = false;
		casovacAnimace.stop();
		casovacAnimace = null;
		vyresetujHru();

	}

	private boolean isKolizeSeZdi(Zed zed, Hrac hrac) {
		return (zed.getMezSpodniCastiZdi().intersects(hrac.getMez()))
				|| (zed.getMezHorniCastiZdi().intersects(hrac.getMez()));
	}

	private boolean isKolizeSHraniciHraciPlochy(Hrac hrac) {
		return (hrac.getY() <= 0) || (hrac.getY() >= HraciPlocha.VYSKA - hrac.getVyskaHrace() - 40);
		// 40-lišta s ikonami maximal. minim, a zavøít
	}

	private void spustHru() {
		casovacAnimace = new Timer(20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				posun();

			}
		});

		nastavZpravuPrazdna();
		hraBezi = true;
		casovacAnimace.start();
	}

	public void pripravHraciPlochu() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					// skok hrace
					hrac.skok();
				}

				// pauza
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (hraBezi) {
						if (pauza) {
							nastavZpravuPrazdna();
							pauza = false;
						} else {
							nastavZpravuPauza();
							pauza = true;

						}
					} else {
						pripravNovouHru();
						spustHru();

					}

				}
			}
		});

		setSize(SIRKA, VYSKA);

	}

	protected void pripravNovouHru() {
		vyresetujHru();

	}

	private void vyresetujHru() {
		resetujVsechnyZdi();
		hrac.reset();
		// nejprve zobraz stare skore, aby hrac videl, kolik bodu nasbiral
		lbSkore.setText(skore + "");
		// ale skore pak vynuluj
		vynulujSkore();
	}

	private void vynulujSkore() {
		skore = 0;

	}

	private void zvedniSkoreZed() {
		skore = skore + Zed.BODY_ZA_ZED;
	}

	private void resetujVsechnyZdi() {
		seznamZdi.clear();
		vyrobZdi(POCET_ZDI);

	}

	private void nastavZpravuNarazuDoZdi() {
		lbZprava.setFont(font);
		lbZprava.setText("narazil jsi do zdi, zksu to znovu");
	}

	private void nastavZpravuPauza() {
		lbZprava.setFont(font);
		lbZprava.setText("pauza");
	}

	private void nastavZpravuOvladani() {
		lbZprava.setFont(fontZpravy);
		lbZprava.setText("pravy klik = start/stop, levy klik = skok");
	}

	private void nastavZpravuPrazdna() {
		lbZprava.setFont(font);
		lbZprava.setText("");
	}

}

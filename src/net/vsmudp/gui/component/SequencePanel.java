package net.vsmudp.gui.component;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.sequence.*;

@SuppressWarnings("serial")
public class SequencePanel extends JPanel implements PropertyName {
	
	protected JTextArea txtURL;
	protected JLabel lblImg;
	protected Icon icon;
	protected Sequence sequence;
	
	private Application app;
	
	private static Border BORDER_PANEL;
	private static Dimension DIMENSION_IMAGE;
	
	private static String IMG_WEB_URL;
	private static String IMG_EMAIL_URL;
	private static String IMG_UNKNOWN_URL;
	
	private static Icon ICON_WEB;
	private static Icon ICON_EMAIL;
	private static Icon ICON_UNKNOWN;
	
	static {
		BORDER_PANEL = new EmptyBorder(0,10,0,2);
		DIMENSION_IMAGE = new Dimension(36,36);
		IMG_WEB_URL = IMG_INTERNET_32;
		IMG_EMAIL_URL = IMG_EMAIL_32;
		IMG_UNKNOWN_URL = IMG_BULB_32;
		ICON_WEB = ICON_EMAIL = null;
	}
	
	public SequencePanel() {
		
		app = Application.getInstance();
		setBorder(BORDER_PANEL);
		setLayout(new BorderLayout(7,0));
		setOpaque(true);
		
		txtURL = new JTextArea();
		txtURL.setEditable(false);
		txtURL.setLineWrap(true);
		txtURL.setWrapStyleWord(false);
		txtURL.setOpaque(false);
		
		lblImg = new JLabel();
		lblImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblImg.setVerticalAlignment(SwingConstants.CENTER);
		lblImg.setPreferredSize(DIMENSION_IMAGE);
		add(lblImg, BorderLayout.LINE_START);
		
		JPanel textBoxHolder = new JPanel();
		textBoxHolder.setLayout(new GridBagLayout());
		textBoxHolder.setOpaque(false);
		
		add(textBoxHolder, BorderLayout.CENTER);
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = 0;
		cons.gridy = 0;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weightx = 1;
		cons.anchor = GridBagConstraints.CENTER;
		
		textBoxHolder.add(txtURL, cons);
		
		icon = null;
		sequence = null;
	}
	
	public void setSequenceData(Sequence aes) {
		sequence = aes;
		int type = aes.getType();
		if (type == Sequence.TYPE_WEB) {
			if (ICON_WEB == null)
				ICON_WEB = new ImageIcon(app.getImage(IMG_WEB_URL));
			icon = ICON_WEB;
		} else if (type == Sequence.TYPE_MAIL) {
			if (ICON_EMAIL == null)
				ICON_EMAIL = new ImageIcon(app.getImage(IMG_EMAIL_URL));
			icon = ICON_EMAIL;
		} else {
			if (ICON_UNKNOWN == null) {
				ICON_UNKNOWN = new ImageIcon(app.getImage(IMG_UNKNOWN_URL));
			}
			icon = ICON_UNKNOWN;
		}
		
		lblImg.setIcon(icon);
		txtURL.setText(sequence.getText());
	}
	
	protected void setColorAsSelected(Color selColor) {
		setBackground(selColor);
	}
	protected void setColorAsNotSelected() {
		setBackground(null);
	}
}
		
		

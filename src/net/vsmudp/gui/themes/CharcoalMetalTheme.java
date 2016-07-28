package net.vsmudp.gui.themes;


import java.awt.Color;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

public class CharcoalMetalTheme extends DefaultMetalTheme implements VsmTheme {

    @Override
	public String getName() { return "Blackbox"; }

    private final ColorUIResource primary1 = new ColorUIResource(33, 66, 66);
    private final ColorUIResource primary2 = new ColorUIResource(66,99,99);
    private final ColorUIResource primary3 = new ColorUIResource(99,99,99);
    
    private final ColorUIResource secondary1 = new ColorUIResource(0,0,0);
    private final ColorUIResource secondary2 = new ColorUIResource(51,51,51);
    private final ColorUIResource secondary3 = new ColorUIResource(102,102,102);
    
    private final ColorUIResource black = new ColorUIResource(Color.white);
    private final ColorUIResource white = new ColorUIResource(Color.black);
    
	private final ColorUIResource me = new ColorUIResource(77, 140, 133);
	private final ColorUIResource re = new ColorUIResource(118, 105, 75);
	private final ColorUIResource ad1 = new ColorUIResource(63,63,63);
	private final ColorUIResource ad2 = ad1;//new ColorUIResource(Color.darkGray);
	
	public ColorUIResource getAdvertisementColor1() { return ad1; }
	public ColorUIResource getAdvertisementColor2() { return ad2; }
	public ColorUIResource getLocalMachineColor() { return me; }
	public ColorUIResource getRemoteMachineColor() { return re; }
    
    @Override
	protected ColorUIResource getPrimary1() { return primary1; }
    @Override
	protected ColorUIResource getPrimary2() { return primary2; }
    @Override
	protected ColorUIResource getPrimary3() { return primary3; }
    
    @Override
	protected ColorUIResource getSecondary1() { return secondary1; }
    @Override
	protected ColorUIResource getSecondary2() { return secondary2; }
    @Override
	protected ColorUIResource getSecondary3() { return secondary3; }
    
    @Override
	protected ColorUIResource getBlack() { return black; }
    @Override
	protected ColorUIResource getWhite() { return white; }

}

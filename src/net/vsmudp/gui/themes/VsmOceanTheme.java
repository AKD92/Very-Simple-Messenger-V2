package net.vsmudp.gui.themes;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

 public class VsmOceanTheme extends OceanTheme implements VsmTheme {
//public class VsmOceanTheme extends OceanTheme implements VsmTheme {
	
	public String getName() { return "Ocean Theme for VSM"; }
	
	private final ColorUIResource me = new ColorUIResource(47, 151, 255);
	private final ColorUIResource re = new ColorUIResource(53, 151, 121);
	private final ColorUIResource ad1 = new ColorUIResource(83, 169, 255);
	private final ColorUIResource ad2 = ad1;//new ColorUIResource(Color.darkGray);
	
	public ColorUIResource getAdvertisementColor1() { return ad1; }
	public ColorUIResource getAdvertisementColor2() { return ad2; }
	public ColorUIResource getLocalMachineColor() { return me; }
	public ColorUIResource getRemoteMachineColor() { return re; }
}

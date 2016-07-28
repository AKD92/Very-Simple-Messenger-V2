package net.vsmudp.gui.component;

import javax.swing.AbstractListModel;
import java.util.List;
import net.vsmudp.engine.sequence.Sequence;


@SuppressWarnings("serial")
public class SequenceListModel extends AbstractListModel {
	
	private List<Sequence> list;
	
	public SequenceListModel(List<Sequence> adrs) {
		list = adrs;
	}
	
	public int getSize() {
		return list.size();
	}
	public Object getElementAt(int index) {
		return list.get(index);
	}
}

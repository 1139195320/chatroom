package com.fy.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class MyCellRenderer extends DefaultListCellRenderer{

	private int[] offset;
	private Font font = new Font("楷体", Font.BOLD, 14);
	
	public MyCellRenderer() {
		
	}
	
	public MyCellRenderer(int[] offset) {
		this.offset = offset;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if(value != null) {
			String str = value.toString();
			setText(str);
			setFont(font);
			setBackground(Color.WHITE);
			if(str.endsWith(" | 在线")) {
				setForeground(Color.RED);
			}else {
				setForeground(Color.PINK);
			}
		}
		if(isSelected) {
			setForeground(Color.CYAN);
			setBackground(Color.LIGHT_GRAY);
		}
		if(offset!=null && offset.length>0) {
			for(int i = 0;i < offset.length ; i ++) {
				if(offset[i] == index) {
					setForeground(Color.GREEN);
				}
			}
		}
		return this;
	}


}

package com.fy.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * @author jack
 */
public class MyCellRenderer extends DefaultListCellRenderer {

	private Integer[] offset;
	private Font font = new Font("楷体", Font.BOLD, 14);

	public MyCellRenderer() {
	}

	public MyCellRenderer(Integer[] offset) {
		this.offset = offset;
	}

	@Override
	public Component getListCellRendererComponent(JList list,
												  Object value,
												  int index,
												  boolean isSelected,
												  boolean cellHasFocus) {
		String str = value.toString();
		setText(str);
		setFont(font);
		setBackground(Color.WHITE);
		if (str.endsWith(" | 在线")) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.PINK);
		}
		if (isSelected) {
			setForeground(Color.CYAN);
			setBackground(Color.LIGHT_GRAY);
		}
		if (offset != null && offset.length > 0) {
			for (Integer idx : offset) {
				if (idx == index) {
					setForeground(Color.GREEN);
				}
			}
		}
		return this;
	}


}

/*
    Copyright (C) Paul Falstad and Iain Sharp

    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lushprojects.circuitjs1.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lushprojects.circuitjs1.client.util.Locale;

import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import static com.google.gwt.event.dom.client.KeyCodes.*;

public class SearchDialog extends Dialog {

	VerticalPanel vp;
	CirSim sim;
	TextArea textArea;
	TextBox textBox;
	Button okButton;
	ListBox listBox;

	public SearchDialog(CirSim asim) {
		super();
		sim = asim;
		this.setWidth("400px");
		this.addStyleName("searchDialog");
		vp=new VerticalPanel();
		vp.setWidth("400px");


		textBox = new TextBox();
		textBox.setMaxLength(30);
		textBox.addStyleName("searchInput");
		vp.add(textBox);
		textBox.addKeyUpHandler(
				new KeyUpHandler() {
					public void onKeyUp(KeyUpEvent ev) {
						int code = ev.getNativeKeyCode();
						if (code == KEY_UP || code == KEY_DOWN) {
							ev.preventDefault();
							return;
						}

						search();
					}
				});

		textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent ev) {
				int code = ev.getNativeKeyCode();
				if (code == KEY_UP || code == KEY_DOWN) {
					ev.preventDefault();

					int idx = listBox.getSelectedIndex();
					int count = listBox.getItemCount();

					String text = "idx: " + idx + " count: " + count;

					if (code == KEY_UP) {
						int newIdx = idx == 0 ? count - 1 : idx - 1;
						listBox.setSelectedIndex(newIdx);
					} else {
						int newIdx = idx == count - 1 ? 0 : idx + 1;
						listBox.setSelectedIndex(newIdx);
					}
				}
			}
		});

		textBox.setWidth("100%");
		textBox.getElement().setPropertyString("placeholder", "Find Component");
		listBox = new ListBox();
		listBox.setWidth("100%");
		listBox.addStyleName("searchList");
		listBox.addDoubleClickHandler(new DoubleClickHandler() {
			public void onDoubleClick(DoubleClickEvent ev) {
				apply();
			}
		});
		listBox.setVisibleItemCount(10);
		vp.add(listBox);
		int i;
		for (i = 0; i != asim.mainMenuItems.size(); i++) {
			CheckboxMenuItem item = sim.mainMenuItems.get(i);
			if (item.getShortcut().length() > 1)
				break;
			listBox.addItem(item.getName());
		}

		setWidget(vp);
		this.center();
		textBox.setFocus(true);

	}

	void apply() {
		String s = listBox.getSelectedItemText();

		int i;
		for (i = 0; i != sim.mainMenuItems.size(); i++) {
			CheckboxMenuItem item = sim.mainMenuItems.get(i);
			if (item.getName().equals(s)) {
				item.getScheduledCommand().execute();
				break;
			}
		}

		closeDialog();
	}

	void search() {
		String str = textBox.getText().toLowerCase();
		int i;
		listBox.clear();
		Vector<String> items = new Vector<String>();
		for (i = 0; i != sim.mainMenuItems.size(); i++) {
			CheckboxMenuItem item = sim.mainMenuItems.get(i);
			if (item.getName().toLowerCase().contains(str)) {
				if (!items.contains(item.getName()))
					items.add(item.getName());
			}
		}
		Collections.sort(items, new Comparator<String>() {
			public int compare(String a, String b) {
				return a.compareTo(b);
			}
		});
		for (i = 0; i != items.size(); i++)
			listBox.addItem(items.get(i));
		if (items.size() > 0)
			listBox.setItemSelected(0, true);
	}
}

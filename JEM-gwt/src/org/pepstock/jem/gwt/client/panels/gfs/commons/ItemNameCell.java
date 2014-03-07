/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.gwt.client.panels.gfs.commons;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

import org.pepstock.jem.GfsFile;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

/**
 * A {@link Cell} used to render text. Clicking on the cell causes its
 * {@link ValueUpdater} to be called.
 */
public class ItemNameCell extends AbstractSafeHtmlCell<GfsFile> {

  /**
   * Construct a new ClickableTextCell that will use a given
   * {@link SafeHtmlRenderer}.
   * 
   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
   */
  public ItemNameCell(SafeHtmlRenderer<GfsFile> renderer) {
    super(renderer, CLICK, KEYDOWN);
  }


  @Override
  public void onBrowserEvent(Context context, Element parent, GfsFile value,
      NativeEvent event, ValueUpdater<GfsFile> valueUpdater) {
    super.onBrowserEvent(context, parent, value, event, valueUpdater);
    if (CLICK.equals(event.getType())) {
      onEnterKeyDown(context, parent, value, event, valueUpdater);
    }
  }

  @Override
  protected void onEnterKeyDown(Context context, Element parent, GfsFile value,
      NativeEvent event, ValueUpdater<GfsFile> valueUpdater) {
    if (valueUpdater != null) {
      valueUpdater.update(value);
    }
  }

  @Override
  protected void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
    if (value != null) {
      sb.append(value);
    }
  }
}
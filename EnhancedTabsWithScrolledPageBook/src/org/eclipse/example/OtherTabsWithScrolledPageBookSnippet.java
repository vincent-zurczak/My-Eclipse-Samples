/**
 * Licensed under the terms of the Eclipse Public License v1.0.
 * See http://www.eclipse.org/legal/epl-v10.html
 * Contributor: Vincent Zurczak
 */

package org.eclipse.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;

/**
 * Horizontal tabs with a {@link ScrolledPageBook}.
 * @author Vincent Zurczak
 */
public class OtherTabsWithScrolledPageBookSnippet {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		
		// Create the shell
		Display display = new Display();
		final Shell shell = new Shell( display );
		shell.setText( "Nice too" );
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		shell.setLayout( layout );
		
		
		// Create a top part
		Composite topComposite = new Composite( shell, SWT.NONE );
		layout = new GridLayout( 2, true );
		layout.marginBottom = 15;
		topComposite.setLayout( layout );
		topComposite.setLayoutData( new GridData( SWT.CENTER, SWT.TOP, true, false ));
		
		File f = new File( System.getProperty( "java.io.tmpdir" ));
		File[] files = f.listFiles();
		if( files == null )
			files = new File[ 0 ];
		
		for( int i=0; i<2; i++ ) {
			TableViewer viewer = new TableViewer( topComposite, SWT.BORDER );
			viewer.setContentProvider( new ArrayContentProvider());
			viewer.setLabelProvider( new LabelProvider() {
				@Override
				public String getText( Object element ) {
					String name = ((File) element).getName();
					if( name.length() > 11 )
						name = name.substring( 0, 10 );
					
					return name;
				}
			});
			
			viewer.setInput( files );
			viewer.getTable().setLayoutData( new GridData( 120, 140 ));
		}
		
		
		// Create the tabs
		Composite downComposite = new Composite( shell, SWT.NONE );
		layout = new GridLayout( 2, false );
		layout.horizontalSpacing = 0;
		downComposite.setLayout( layout );
		downComposite.setLayoutData( new GridData( GridData.FILL_BOTH ));
		
		new OtherTabsWithScrolledPageBookSnippet().createTabs( downComposite );
		
		
		// Open the shell
		shell.setSize( 600, 400 );
		shell.open ();
		while( ! shell.isDisposed()) {
			if( ! display.readAndDispatch()) 
				display.sleep();
		}
		
		display.dispose();
	}

	
	/**
	 * Remember the selected index.
	 */
	private Integer selectedIndex = 0;
	
	
	/**
	 * Creates the tabs.
	 * @param parent the parent
	 */
	public void createTabs( Composite parent ) {
		
		// Add the container for navigation labels
		final int tabCpt = 5;
		Composite container = new Composite( parent, SWT.NONE );
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		container.setLayout( layout );
		container.setLayoutData( new GridData( GridData.FILL_VERTICAL ));
		
		// Add the page book
		final ScrolledPageBook pageBook = new ScrolledPageBook( parent );
		pageBook.setLayoutData( new GridData( GridData.FILL_BOTH ));
		
		// The listener when the user clicks on a "tab"
		final List<Label> navigationLabels = new ArrayList<Label> ();
		Listener listener = new Listener() {
			@Override
			public void handleEvent( Event event ) {
				pageBook.showPage( event.widget.getData());
				
				// Remember the last selected index
				OtherTabsWithScrolledPageBookSnippet.this.selectedIndex = (Integer) event.widget.getData();
				
				// Highlight the selected tab
				for( Label l : navigationLabels ) {
					Color color;
					if( l != event.widget && l.getParent() != event.widget )
						color = Display.getDefault().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND );
					else
						color = Display.getDefault().getSystemColor( SWT.COLOR_WHITE );
						
					l.setBackground( color );
					l.getParent().setBackground( color );
				}
			}
		};
		
		// The paint listener, to paint partial borders
		PaintListener paintListener = new PaintListener() {
			@Override
			public void paintControl( PaintEvent event ) {
				
				Color color;
				if( OtherTabsWithScrolledPageBookSnippet.this.selectedIndex.equals( event.widget.getData()))
					color = Display.getDefault().getSystemColor( SWT.COLOR_GRAY );
				else
					color = Display.getDefault().getSystemColor( SWT.COLOR_WIDGET_DARK_SHADOW );
				
				Rectangle rect = ((Composite) event.widget).getBounds();
				event.gc.setForeground( color );
				event.gc.setAntialias( SWT.ON );
				event.gc.drawLine( 0, 0, rect.width - 1, 0 );
				event.gc.drawLine( 0, 0, 0, rect.height - 1 );
				event.gc.drawLine( 0, rect.height - 1, rect.width - 1, rect.height - 1 );
			}
		};
		
		// Register the pages and bind it all
		for( int i=0; i<tabCpt; i++ ) {
			Label l = createTabLabel( i, container, paintListener, listener );
			navigationLabels.add( l );
			pageBook.registerPage( i, createTabContent( pageBook.getContainer(), i ));
		}
		
		// Force to display the first tab (and force it to be highlighted)
		navigationLabels.get( 0 ).notifyListeners( SWT.MouseDown, new Event());
	}
	
	
	/**
	 * Creates a label for the tab (wrapped in a composite for better display).
	 * @param index the tab index
	 * @param parent the container for the label
	 * @param paintListener the paint listener for the label's container (to paint partial borders)
	 * @param mouseDownListener the listener for when a tab is selected
	 * @return the created label
	 */
	private Label createTabLabel( int index, Composite parent, PaintListener paintListener, Listener mouseDownListener ) {
		
		// Wrap the labels in a composite
		Composite c = new Composite( parent, SWT.NONE );
		c.setLayout( new GridLayout());
		c.setLayoutData( new GridData( 80, 25 ));
		c.setData( index );

		// To paint partial borders
		c.addPaintListener( paintListener );

		// Deal with the content
		Label l = new Label( c, SWT.NONE );
		l.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true ));
		l.setText( "Item " + index );
		l.setData( index );

		// The click listener
		l.addListener( SWT.MouseDown, mouseDownListener );
		c.addListener( SWT.MouseDown, mouseDownListener );

		return l;
	}
	
	
	/**
	 * Creates the content of a tab.
	 * @param parent the parent
	 */
	private Composite createTabContent( Composite parent, int id ) {
		
		// Paint borders around the composite (content of the tab)
		Composite c = new Composite( parent, SWT.BORDER );
		c.setLayout( new GridLayout( 2, false ));
		String[] properties = { "First Name:", "Last Name:", "Nick Name:" };
		
		for( String property : properties ) {
			new Label( c, SWT.NONE ).setText( property );
			Text t = new Text( c, SWT.SINGLE | SWT.BORDER );
			t.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
			t.setText( "Showing " +  String.valueOf( id ));
		}
		
		return c;
	}
}

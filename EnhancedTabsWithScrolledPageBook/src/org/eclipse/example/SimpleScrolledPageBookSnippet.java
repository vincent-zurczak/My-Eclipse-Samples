/**
 * Licensed under the terms of the Eclipse Public License v1.0.
 * See http://www.eclipse.org/legal/epl-v10.html
 * Contributor: Vincent Zurczak
 */

package org.eclipse.example;

import java.io.File;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
 * A first try with {@link ScrolledPageBook}.
 * @author Vincent Zurczak
 */
public class SimpleScrolledPageBookSnippet {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		
		// Create the shell
		Display display = new Display();
		final Shell shell = new Shell( display );
		shell.setText( "Everything is centered... but it's ugly... for the moment" );
		shell.setLayout( new GridLayout());
		
		
		// Create a top part
		Composite topComposite = new Composite( shell, SWT.NONE );
		topComposite.setLayout( new GridLayout( 2, true ));
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
		new SimpleScrolledPageBookSnippet().createTabs( shell );
		
		
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
	 * Creates the tabs.
	 * @param parent the parent
	 */
	public void createTabs( Composite parent ) {
		
		// Add the container for navigation labels
		final int tabCpt = 5;
		Composite container = new Composite( parent, SWT.NONE );
		container.setLayout( new GridLayout( tabCpt, true ));
		container.setLayoutData( new GridData( SWT.CENTER, SWT.DEFAULT, true, false ));
		
		// Add the page book
		final ScrolledPageBook pageBook = new ScrolledPageBook( parent );
		pageBook.setLayoutData( new GridData( GridData.FILL_BOTH ));
		
		// The listener when the user clicks on a "tab"
		Listener listener = new Listener() {
			@Override
			public void handleEvent( Event event ) {
				pageBook.showPage( event.widget.getData());
			}
		};
		
		// Register the pages and bind it all
		for( int i=0; i<tabCpt; i++ ) {
			Label l = new Label( container, SWT.BORDER );
			l.setText( "Item " + i );
			l.setData( i );
			
			pageBook.registerPage( i, createTabContent( pageBook.getContainer(), i ));
			l.addListener( SWT.MouseDown, listener );
		}
		
		// Force to display the first tab
		pageBook.showPage( 0 );
	}
	
	
	/**
	 * Creates the content of a tab.
	 * @param parent the parent
	 */
	private Composite createTabContent( Composite parent, int id ) {
		
		Composite c = new Composite( parent, SWT.NONE );
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
